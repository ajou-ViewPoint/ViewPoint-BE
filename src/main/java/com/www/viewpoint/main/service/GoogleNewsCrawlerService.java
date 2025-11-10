package com.www.viewpoint.main.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoogleNewsCrawlerService {

    private static final String UA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private final GeminiClient geminiClient;

    public GoogleNewsCrawlerService(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    public record Article(String title, String link, String source, String content) { }

    private record NewsCard(String title, String link, String source) { }

    private String buildSystemPrompt() {
        return """
        역할: 당신은 뉴스 본문 추출기다.
        입력: 웹페이지의 전체 HTML 또는 원문 텍스트가 주어진다.
        목표: 기사 본문만 깔끔한 문단 텍스트로 추출하여 줄글로 요약.
        규칙:
        - 광고, 구독 유도, 추천/인기 기사, '돌아가기', 해시태그, 저작권 고지, 댓글 유도, 상하단 네비 제거.
        - 버튼 관련 텍스트 제거.
        - 중복 문장/문단 제거.
        - 출력은 순수 본문 기사 텍스트만(plain text).
        """;
    }

    private List<NewsCard> fetchGoogleNewsCards(String query, int limit) throws Exception {
        String url = "https://www.google.com/search?q="
                + URLEncoder.encode(query, StandardCharsets.UTF_8)
                + "&tbm=nws&tbs=qdr:d&hl=ko&gl=KR&lr=lang_ko";

        Document doc = Jsoup.connect(url)
                .userAgent(UA)
                .referrer("https://www.google.com/")
                .timeout(8000)
                .get();

        List<NewsCard> result = new ArrayList<>();

        for (Element cardEl : doc.select("div.SoaBEf, div.dbsr, div.xuvV6b")) {
            Element a = cardEl.selectFirst("a.WlydOe, a");
            Element ttl = cardEl.selectFirst("div.MBeuO, div.n0jPhd, div[role=heading]");
            Element src = cardEl.selectFirst("div.CEMjEf, div.SVJrMe, div.MVoHrc, span.xQ82C");

            String link = a != null ? a.attr("href") : "";
            String title = ttl != null ? ttl.text() : "(제목 없음)";
            String source = src != null ? src.text() : "(언론사 없음)";

            if (!link.isBlank()) {
                result.add(new NewsCard(title, link, source));
            }

            if (result.size() >= limit) break;
        }

        if (result.isEmpty()) {
            log.warn("No news cards found for query={}", query);
        } else {
            log.info("Fetched {} news cards for query={}", result.size(), query);
        }

        return result;
    }

    public Article getTopNewsWithGemini() {
        final int MAX_CANDIDATES = 5;
        final int MIN_VALID_BODY = 1200;
        final int MIN_PRE_EXTRACT_FOR_GEMINI = 3000;
        final int MAX_GEMINI_INPUT = 60000;

        List<NewsCard> cards;
        try {
            cards = fetchGoogleNewsCards("법안", MAX_CANDIDATES);
        } catch (Exception e) {
            log.error("Failed to fetch Google news cards", e);
            return new Article("(제목 없음)", "", "(언론사 없음)", "(본문 내용 없음)");
        }

        if (cards == null || cards.isEmpty()) {
            return new Article("(제목 없음)", "", "(언론사 없음)", "(본문 내용 없음)");
        }

        Article bestFallback = null;

        for (NewsCard card : cards) {
            String renderedHtml = "";
            String preExtract = "";

            try {
                log.info("Trying card: {}", card);

                renderedHtml = fetchRenderedHtml(card.link());
                int htmlLen = renderedHtml != null ? renderedHtml.length() : 0;
                log.info("Rendered HTML length = {}", htmlLen);

                if (htmlLen < 500) {
                    log.info("Skip card (too short html): {}", card.link());
                    continue; // 영상/에러 가능성 높음 → 다음 기사
                }

                preExtract = preExtractLikelyBody(renderedHtml);
                int preLen = preExtract != null ? preExtract.length() : 0;
                log.info("PreExtract length = {}", preLen);

                // 영상 기사 등: 본문이 거의 없으면 이 카드 스킵
                if (preLen < MIN_VALID_BODY) {
                    log.info("Skip card (preExtract too short): {}", card.link());
                    // fallback 후보로는 남겨둘 수 있음
                    if (bestFallback == null) {
                        String fallbackText = (preExtract != null && !preExtract.isBlank())
                                ? preExtract.trim()
                                : Jsoup.parse(renderedHtml).text();
                        bestFallback = new Article(
                                card.title(), card.link(), card.source(),
                                (fallbackText != null && !fallbackText.isBlank())
                                        ? fallbackText
                                        : "(본문 내용 없음)"
                        );
                    }
                    continue;
                }

                // Gemini에 넘길 입력 결정
                String geminiInput = preLen >= MIN_PRE_EXTRACT_FOR_GEMINI
                        ? preExtract
                        : (htmlLen > MAX_GEMINI_INPUT
                        ? renderedHtml.substring(0, MAX_GEMINI_INPUT)
                        : renderedHtml);

                log.info("Gemini input length(final) = {}", geminiInput.length());

                String finalBody = "";
                if (!geminiInput.isBlank()) {
                    try {
                        finalBody = geminiClient.extractArticlePlain(buildSystemPrompt(), geminiInput);
                    } catch (Exception llmErr) {
                        log.warn("Gemini call failed for card {}, fallback to preExtract/html. reason={}",
                                card.link(), llmErr.toString());
                    }
                }

                // Gemini 결과 검증
                if (finalBody == null || finalBody.trim().length() < MIN_VALID_BODY) {
                    // Gemini 결과 별로면 preExtract 사용
                    finalBody = preExtract;
                }

                if (finalBody != null && finalBody.trim().length() >= MIN_VALID_BODY) {

                    String cleaned = finalBody.trim();
                    log.info("Selected card {} with final body length = {}", card.link(), cleaned.length());
                    return new Article(card.title(), card.link(), card.source(), cleaned);
                } else {
                    log.info("Skip card (final body too short): {}", card.link());
                }

            } catch (Exception e) {
                log.warn("Error while processing card {}: {}", card, e.toString());
            }
        }

        // 후보들 다 별로였으면, 그 중 그나마 첫 fallback 사용
        if (bestFallback != null) {
            log.info("Using bestFallback article: {}", bestFallback.link());
            return bestFallback;
        }

        // 진짜 아무 것도 못 건지면
        NewsCard first = cards.get(0);
        return new Article(first.title(), first.link(), first.source(), "(본문 내용 없음)");
    }

    private NewsCard fetchTopGoogleNewsCard(String query) throws Exception {
        String url = "https://www.google.com/search?q="
                + URLEncoder.encode(query, StandardCharsets.UTF_8)
                + "&tbm=nws&tbs=qdr:d";

        Document doc = Jsoup.connect(url)
                .userAgent(UA)
                .referrer("https://www.google.com/")
                .timeout(8000)
                .get();

        Element first = doc.selectFirst("div.SoaBEf, div.dbsr, div.xuvV6b");
        if (first == null) {
            log.warn("No first news card found for query={}", query);
            return null;
        }

        Element a = first.selectFirst("a.WlydOe, a");
        Element ttl = first.selectFirst("div.MBeuO, div.n0jPhd, div[role=heading]");
        Element src = first.selectFirst("div.CEMjEf, div.SVJrMe, div.MVoHrc, span.xQ82C");

        return new NewsCard(
                ttl != null ? ttl.text() : "(제목 없음)",
                a != null ? a.attr("href") : "",
                src != null ? src.text() : "(언론사 없음)"
        );
    }

    private String fetchRenderedHtml(String url) throws Exception {
        if (url == null || url.isBlank()) {
            return "";
        }

        try {
            // Jsoup으로 직접 HTML 요청
            Connection connection = Jsoup.connect(url)
                    .userAgent(UA)
                    .referrer("https://www.google.com/")
                    .header("Accept-Language", "ko-KR,ko;q=0.9")
                    .timeout(10000)  // 10초 타임아웃
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true);

            Document doc = connection.get();

            // JS 없이 렌더링된 HTML 반환
            String html = doc.outerHtml();

            // HTML이 너무 짧으면 실패 처리
            if (html == null || html.length() < 500) {
                log.warn("Jsoup fetch returned unusually short HTML ({} chars)", html.length());
            }

            return html;

        } catch (Exception e) {
            log.error("fetchRenderedHtml() failed via Jsoup: {}", e.getMessage(), e);
            return "";
        }
    }

    private String preExtractLikelyBody(String html) {
        if (html == null || html.isBlank()) return "";

        try {
            Document doc = Jsoup.parse(html);

            // 1. 노이즈 엘리먼트 제거
            doc.select("""
                script, style, iframe, noscript, aside, figure, header, footer,
                .ad, [class*=ad], [id*=ad], [class*=banner], [id*=banner],
                [class*=recommend], [id*=recommend], [class*=related], [id*=related],
                [class*=subscribe], [id*=subscribe], [class*=popular], [id*=popular],
                nav, .sns, .share, [class*=sns], [id*=sns], .social, [class*=footer]
                """).remove();

            List<String> chunks = new ArrayList<>();

            // 2. <article> 전체 수집
            for (Element e : doc.select("article")) {
                String t = e.text().trim();
                if (t.length() >= 50) {
                    chunks.add(t);
                }
            }

            // 3. id가 article* 인 div/section 전체 수집
            for (Element e : doc.select("div[id^=article], section[id^=article]")) {
                String t = e.text().trim();
                if (t.length() >= 50) {
                    chunks.add(t);
                }
            }

            // 4. class에 article 포함된 컨테이너
            for (Element e : doc.select("div[class*=article], section[class*=article]")) {
                String t = e.text().trim();
                if (t.length() >= 80) {
                    chunks.add(t);
                }
            }

            // 5. HTML 주석 중 "기사 본문" 힌트 활용
            //    <!-- 기사 본문 -->, <!-- 기사 본분 --> 등 오타도 포함해서 검사
            doc.traverse(new NodeVisitor() {
                @Override
                public void head(Node node, int depth) {
                    if (node.nodeName().equals("#comment")) {
                        String c = node.toString(); // <!-- ... -->
                        String lower = c.toLowerCase();
                        if (lower.contains("기사 본문") || lower.contains("기사 본분")) {
                            // (1) 부모 엘리먼트 텍스트
                            if (node.parent() instanceof Element parent) {
                                String t = parent.text().trim();
                                if (t.length() >= 50) {
                                    chunks.add(t);
                                }
                            }
                            // (2) 다음 형제 엘리먼트(실제 본문 블록인 경우 많음)
                            Node sib = node.nextSibling();
                            if (sib instanceof Element sibling) {
                                String t = sibling.text().trim();
                                if (t.length() >= 50) {
                                    chunks.add(t);
                                }
                            }
                        }
                    }
                }

                @Override
                public void tail(Node node, int depth) { }
            });

            // 6. 중복 제거 + 합치기
            LinkedHashSet<String> unique = new LinkedHashSet<>(chunks);
            String combined = String.join("\n", unique).trim();

            if (combined.length() >= 150) {
                return cleanBodyText(combined);
            }

            // 7. 부족하면 특정 영역 p 태그 기반
            String pJoined = doc.select("article p, div[id^=article] p, section[id^=article] p, div[class*=article] p")
                    .stream()
                    .map(el -> el.text().trim())
                    .filter(s -> s.length() > 10)
                    .distinct()
                    .collect(Collectors.joining("\n"));

            if (pJoined.length() >= 150) {
                return cleanBodyText(pJoined);
            }

            // 8. 그래도 안 되면 전체 p 기반
            String allP = doc.select("p")
                    .stream()
                    .map(el -> el.text().trim())
                    .filter(s -> s.length() > 20)
                    .distinct()
                    .collect(Collectors.joining("\n"));

            if (allP.length() >= 150) {
                return cleanBodyText(allP);
            }

            // 9. 최후 fallback: 전체 텍스트
            return cleanBodyText(doc.text());

        } catch (Exception e) {
            log.warn("preExtractLikelyBody failed", e);
            return "";
        }
    }

    private String cleanBodyText(String text) {
        if (text == null) return "";
        return text
                .replaceAll("저작권자.?\\s*©?.*?무단전재.*?(금지)?", " ")
                .replaceAll("이 기사를 공유합니다.*", " ")
                .replaceAll("SNS 기사보내기.*", " ")
                .replaceAll("공유하기.*", " ")
                .replaceAll("(돌아가기\\s*)+", " ")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }
}