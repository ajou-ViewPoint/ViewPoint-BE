package com.www.viewpoint.main.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleNewsCrawlerService {

    private static final String UA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private final GeminiClient geminiClient;
    private final ImagenClient imagenClient;

    // ✅ 이미지까지 포함하는 Article
    public record Article(
            String title,
            String link,
            String source,
            String content,
            List<ImagenClient.GeneratedImage> images
    ) { }

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
            return new Article(
                    "(제목 없음)",
                    "",
                    "(언론사 없음)",
                    "(본문 내용 없음)",
                    Collections.emptyList()
            );
        }

        if (cards == null || cards.isEmpty()) {
            return new Article(
                    "(제목 없음)",
                    "",
                    "(언론사 없음)",
                    "(본문 내용 없음)",
                    Collections.emptyList()
            );
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
                    continue; // 영상/에러 가능성 높음
                }

                preExtract = preExtractLikelyBody(renderedHtml);
                int preLen = preExtract != null ? preExtract.length() : 0;
                log.info("PreExtract length = {}", preLen);

                if (preLen < MIN_VALID_BODY) {
                    log.info("Skip card (preExtract too short): {}", card.link());

                    if (bestFallback != null) {
                        log.info("Using bestFallback article: {}", bestFallback.link());

                        String imagePrompt = buildImagenPromptFromArticle(
                                bestFallback.title(),
                                bestFallback.content()
                        );

                        List<ImagenClient.GeneratedImage> images = Collections.emptyList();
                        if (imagePrompt != null && !imagePrompt.isBlank()) {
                            try {
                                images = imagenClient.generateImages(imagePrompt, 1);
                            } catch (Exception imgErr) {
                                log.warn("Imagen generation failed for bestFallback {}, reason={}",
                                        bestFallback.link(), imgErr.toString());
                            }
                        }

                        return new Article(
                                bestFallback.title(),
                                bestFallback.link(),
                                bestFallback.source(),
                                bestFallback.content(),
                                images
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

                if (finalBody == null || finalBody.trim().length() < MIN_VALID_BODY) {
                    finalBody = preExtract;
                }


                if (finalBody != null && finalBody.trim().length() >= MIN_VALID_BODY) {

                    String cleaned = finalBody.trim();
                    log.info("Selected card {} with final body length = {}", card.link(), cleaned.length());

                    // ✅ 기사 전체를 활용해 Gemini로부터 Imagen용 프롬프트 생성
                    String imagePrompt = buildImagenPromptFromArticle(card.title(), cleaned);

                    List<ImagenClient.GeneratedImage> images = Collections.emptyList();
                    if (imagePrompt != null && !imagePrompt.isBlank()) {
                        try {
                            images = imagenClient.generateImages(imagePrompt, 1);
                        } catch (Exception imgErr) {
                            log.warn("Imagen generation failed for card {}, reason={}", card.link(), imgErr.toString());
                        }
                    } else {
                        log.warn("Image prompt from Gemini was empty, skipping Imagen call");
                    }

                    return new Article(
                            card.title(),
                            card.link(),
                            card.source(),
                            cleaned,
                            images
                    );
                } else {
                    log.info("Skip card (final body too short): {}", card.link());
                }

            } catch (Exception e) {
                log.warn("Error while processing card {}: {}", card, e.toString());
            }
        }

        // 후보들 다 별로였으면 fallback 사용
        if (bestFallback != null) {
            log.info("Using bestFallback article: {}", bestFallback.link());

            String prompt = buildImagePrompt(bestFallback.title(), bestFallback.content());
            List<ImagenClient.GeneratedImage> images;
            try {
                images = imagenClient.generateImages(prompt, 1);
            } catch (Exception imgErr) {
                log.warn("Imagen generation failed for bestFallback {}, reason={}",
                        bestFallback.link(), imgErr.toString());
                images = Collections.emptyList();
            }

            return new Article(
                    bestFallback.title(),
                    bestFallback.link(),
                    bestFallback.source(),
                    bestFallback.content(),
                    images
            );
        }

        // 진짜 아무 것도 못 건지면
        NewsCard first = cards.get(0);
        String fallbackContent = "(본문 내용 없음)";

        List<ImagenClient.GeneratedImage> images;
        try {
            images = imagenClient.generateImages(
                    buildImagePrompt(first.title(), fallbackContent),
                    1
            );
        } catch (Exception imgErr) {
            log.warn("Imagen generation failed for ultimate fallback {}, reason={}",
                    first.link(), imgErr.toString());
            images = Collections.emptyList();
        }

        return new Article(
                first.title(),
                first.link(),
                first.source(),
                fallbackContent,
                images
        );
    }

    // 기사 제목 + 본문 앞부분으로 이미지 프롬프트 생성
    private String buildImagePrompt(String title, String body) {
        String safeBody = body == null ? "" : body;
        String safeTitle = (title == null || title.isBlank()) ? "" : title;

        String prefix = safeTitle.isBlank() ? "" : safeTitle + " - ";
        String truncatedBody = safeBody.length() > 600
                ? safeBody.substring(0, 600)
                : safeBody;

        return prefix + truncatedBody;
    }

    private String fetchRenderedHtml(String url) throws Exception {
        if (url == null || url.isBlank()) {
            return "";
        }

        try {
            Connection connection = Jsoup.connect(url)
                    .userAgent(UA)
                    .referrer("https://www.google.com/")
                    .header("Accept-Language", "ko-KR,ko;q=0.9")
                    .timeout(10000)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true);

            Document doc = connection.get();

            String html = doc.outerHtml();

            if (html == null || html.length() < 500) {
                log.warn("Jsoup fetch returned unusually short HTML ({} chars)", html == null ? 0 : html.length());
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

            doc.select("""
                script, style, iframe, noscript, aside, figure, header, footer,
                .ad, [class*=ad], [id*=ad], [class*=banner], [id*=banner],
                [class*=recommend], [id*=recommend], [class*=related], [id*=related],
                [class*=subscribe], [id*=subscribe], [class*=popular], [id*=popular],
                nav, .sns, .share, [class*=sns], [id*=sns], .social, [class*=footer]
                """).remove();

            List<String> chunks = new ArrayList<>();

            for (Element e : doc.select("article")) {
                String t = e.text().trim();
                if (t.length() >= 50) {
                    chunks.add(t);
                }
            }

            for (Element e : doc.select("div[id^=article], section[id^=article]")) {
                String t = e.text().trim();
                if (t.length() >= 50) {
                    chunks.add(t);
                }
            }

            for (Element e : doc.select("div[class*=article], section[class*=article]")) {
                String t = e.text().trim();
                if (t.length() >= 80) {
                    chunks.add(t);
                }
            }

            doc.traverse(new NodeVisitor() {
                @Override
                public void head(Node node, int depth) {
                    if (node.nodeName().equals("#comment")) {
                        String c = node.toString();
                        String lower = c.toLowerCase();
                        if (lower.contains("기사 본문") || lower.contains("기사 본분")) {
                            if (node.parent() instanceof Element parent) {
                                String t = parent.text().trim();
                                if (t.length() >= 50) {
                                    chunks.add(t);
                                }
                            }
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

            LinkedHashSet<String> unique = new LinkedHashSet<>(chunks);
            String combined = String.join("\n", unique).trim();

            if (combined.length() >= 150) {
                return cleanBodyText(combined);
            }

            String pJoined = doc.select("article p, div[id^=article] p, section[id^=article] p, div[class*=article] p")
                    .stream()
                    .map(el -> el.text().trim())
                    .filter(s -> s.length() > 10)
                    .distinct()
                    .collect(Collectors.joining("\n"));

            if (pJoined.length() >= 150) {
                return cleanBodyText(pJoined);
            }

            String allP = doc.select("p")
                    .stream()
                    .map(el -> el.text().trim())
                    .filter(s -> s.length() > 20)
                    .distinct()
                    .collect(Collectors.joining("\n"));

            if (allP.length() >= 150) {
                return cleanBodyText(allP);
            }

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

    private String buildImagenPromptFromArticle(String title, String article) {
        if (article == null || article.isBlank()) {
            return null;
        }

        // 기사 제목도 같이 주면 프롬프트 만들 때 도움될 수 있음
        String articleWithTitle = (title == null || title.isBlank())
                ? article
                : "기사 제목: " + title + "\n\n기사 본문:\n" + article;

        String systemPrompt = """
    역할: 당신은 이미지 생성용 프롬프트를 작성하는 도우미다.

    아래의 기사를 한 차례 자체적으로 요약한 후 요구사항을 수행하되 요청한 프롬프트 이외의 것은 출력하지 마라.

    위에서 제공한 기사에 등장하는 법안을 설명하는 이미지를 만들려고 한다.
    이에 이미지를 가장 효율적으로 만들 수 있는 프롬프트를 작성하라.
    프롬프트에는 기사 안에서 법안의 등장 맥락 혹은 내용을 추출해서 이미지에 반영하라.
    이미지에는 글자를 포함시키지 말고 그림을 활용해서 직관적으로 만들어라.
    실사보다는 아이콘을 활용해서 인포그래픽처럼 생성하라.
    이미지는 법안관련 카드뉴스에 활용할 것이다.
    크기는 16*9로 만들도록 프롬프트를 작성하라.
    """;

        try {
            // ✅ 여기서는 "기사 전체"를 그대로 두 번째 인자로 넘김 (자르지 않음)
            String prompt = geminiClient.extractArticlePlain(systemPrompt, articleWithTitle);
            if (prompt != null) {
                prompt = prompt.trim();
            }
            return prompt;
        } catch (Exception e) {
            log.warn("Failed to build Imagen prompt via Gemini", e);
            // 완전 실패하면 아주 단순한 fallback 프롬프트
            if (title != null && !title.isBlank()) {
                return "Infographic style 16:9 illustration explaining a Korean bill: " + title;
            }
            return "Infographic style 16:9 illustration explaining a Korean bill, using icons and no text.";
        }
    }
}