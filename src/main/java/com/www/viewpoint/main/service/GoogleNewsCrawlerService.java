package com.www.viewpoint.main.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
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
        목표: 기사 본문만 깔끔한 문단 텍스트로 추출한다.
        규칙:
        - 광고, 구독 유도, 추천/인기 기사, '돌아가기', 해시태그, 저작권 고지, 댓글 유도, 상하단 네비 제거.
        - 제목/부제/본문만 남기되 문단 구분은 줄바꿈 유지.
        - 중복 문장/문단 제거. 요약/창작 금지.
        - 출력은 순수 본문 텍스트만(plain text).
        """;
    }

    public Article getTopNewsWithGemini() {
        NewsCard card = null;
        String renderedHtml = "";
        String preExtract = "";

        // 최소 본문 길이 기준 (이 이하면 실패로 보고 다른 소스 사용)
        final int MIN_VALID_BODY = 120;        // 필요하면 200~300으로 올려도 됨
        final int MIN_PRE_EXTRACT_FOR_GEMINI = 300; // 이 이하면 preExtract 대신 HTML 전체를 LLM에 줌
        final int MAX_GEMINI_INPUT = 60000;    // LLM에 넘길 최대 길이(과도한 토큰 방지)

        try {
            // 1. 구글 뉴스 상단 카드 가져오기
            card = fetchTopGoogleNewsCard("법안");
            log.info("Top news card: {}", card);

            if (card == null) {
                return new Article("(제목 없음)", "", "(언론사 없음)", "(본문 내용 없음)");
            }

            // 2. 렌더링 HTML 가져오기
            renderedHtml = fetchRenderedHtml(card.link());
            int htmlLen = (renderedHtml != null) ? renderedHtml.length() : 0;
            log.info("Rendered HTML length = {}", htmlLen);

            // 3. 사전 추출 (셀렉터 기반)
            preExtract = preExtractLikelyBody(renderedHtml);
            int preLen = (preExtract != null) ? preExtract.length() : 0;
            log.info("PreExtract length = {}", preLen);

            // 4. Gemini에 넘길 입력 결정 로직 (여기가 핵심 수정)
            String geminiInput;
            if (preLen >= MIN_PRE_EXTRACT_FOR_GEMINI) {
                // 충분히 긴 본문 후보면 그걸 사용
                geminiInput = preExtract;
            } else if (htmlLen > 0) {
                // preExtract가 너무 짧으면 전체 HTML(or text)을 사용
                // 너무 길면 앞부분만 잘라서 전달
                String src = renderedHtml;
                if (htmlLen > MAX_GEMINI_INPUT) {
                    src = renderedHtml.substring(0, MAX_GEMINI_INPUT);
                }
                geminiInput = src;
            } else {
                geminiInput = "";
            }

            log.info("Gemini input length(final) = {}", geminiInput.length());

            String finalBody = "";

            // 5. Gemini 호출 (입력이 있을 때만)
            if (!geminiInput.isBlank()) {
                try {
                    finalBody = geminiClient.extractArticlePlain(buildSystemPrompt(), geminiInput);
                } catch (Exception llmErr) {
                    log.warn("Gemini call failed, fallback to preExtract/html. reason={}", llmErr.toString());
                }
            }

            // 6. Gemini 결과가 짧으면 실패로 보고 fallback
            if (finalBody == null || finalBody.trim().length() < MIN_VALID_BODY) {
                // 1순위: preExtract가 그나마 길다면 사용
                if (preLen >= MIN_VALID_BODY) {
                    finalBody = preExtract;
                }
                // 2순위: 전체 HTML에서 텍스트만 뽑기
                else if (htmlLen > 0) {
                    String text = Jsoup.parse(renderedHtml).text();
                    finalBody = (text != null && text.trim().length() >= MIN_VALID_BODY)
                            ? text
                            : "";
                } else {
                    finalBody = "";
                }
            }

            log.info("Final body length = {}", (finalBody != null ? finalBody.length() : 0));

            // 7. 그래도 없으면 명시적으로 표시
            if (finalBody == null || finalBody.trim().isEmpty()) {
                finalBody = "(본문 내용 없음)";
            }

            return new Article(
                    card.title(),
                    card.link(),
                    card.source(),
                    finalBody.trim()
            );

        } catch (Exception e) {
            log.error("getTopNewsWithGemini() failed, returning best-effort fallback", e);

            String title = (card != null) ? card.title() : "(제목 없음)";
            String link = (card != null) ? card.link() : "";
            String src  = (card != null) ? card.source() : "(언론사 없음)";

            String fallback;
            if (preExtract != null && preExtract.trim().length() >= MIN_VALID_BODY) {
                fallback = preExtract.trim();
            } else if (renderedHtml != null && !renderedHtml.isBlank()) {
                String text = Jsoup.parse(renderedHtml).text();
                fallback = (text != null && text.trim().length() >= MIN_VALID_BODY)
                        ? text.trim()
                        : "(본문 내용 없음)";
            } else {
                fallback = "(본문 내용 없음)";
            }

            return new Article(title, link, src, fallback);
        }
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

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.addArguments(
                "--headless",                    // 안정적인 headless
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1366,768",
                "--lang=ko-KR",
                "--user-agent=" + UA,
                "--disable-blink-features=AutomationControlled",
                "--remote-allow-origins=*"
        );
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        try {
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(25));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(25));

            driver.get(url);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // 1) 문서 로딩 완료 대기
            try {
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .pollingEvery(Duration.ofMillis(300))
                        .until(d -> "complete".equals(
                                js.executeScript("return document.readyState")));
            } catch (TimeoutException ignore) {
                log.debug("readyState complete 대기 타임아웃 (무시)");
            }

            // 2) 본문 후보 등장 or 텍스트 충분히 쌓일 때까지 대기
            try {
                new WebDriverWait(driver, Duration.ofSeconds(8))
                        .pollingEvery(Duration.ofMillis(300))
                        .until(d -> {
                            String script =
                                    "const sels = [" +
                                            "'div#newsct_article','div#dic_area','article'," +
                                            "'div#articleBody','div#article_body','div#article-view-content-div'," + // eInfomax 등
                                            "'div[itemprop=articleBody]','section.article-body'," +
                                            "'div.article-body','div#content','div#contents','div#container'" +
                                            "];" +
                                            "for (const s of sels) {" +
                                            "if (document.querySelector(s)) return true;" +
                                            "}" +
                                            "return document.body && document.body.innerText && " +
                                            "document.body.innerText.length > 1500;";
                            Object ok = js.executeScript(script);
                            return Boolean.TRUE.equals(ok);
                        });
            } catch (TimeoutException ignore) {
                log.debug("본문 후보/텍스트 길이 대기 타임아웃 (무시)");
            }

            String html = driver.getPageSource();

            // 3) 너무 짧으면 한 번 더 확인
            if (html == null || html.length() < 1000) {
                try {
                    Thread.sleep(1500L);
                } catch (InterruptedException ignored) { }
                String retry = driver.getPageSource();
                if (retry != null && retry.length() > (html == null ? 0 : html.length())) {
                    html = retry;
                }
            }

            return html != null ? html : "";
        } finally {
            try {
                driver.quit();
            } catch (Exception ignore) { }
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
                    [class*=subscribe], [id*=subscribe], [class*=popular], [id*=popular]
                    """).remove();

            String[] candidates = {
                    "div#newsct_article","div#dic_area","article",
                    "div#articleBody","div#article_body",
                    "div[itemprop=articleBody]","section.article-body",
                    "div.article-body","div#content","div#contents","div#container"
            };

            String best = "";
            int bestLen = 0;

            for (String sel : candidates) {
                for (Element e : doc.select(sel)) {
                    String text = e.text().trim();
                    if (text.length() > bestLen) {
                        best = text;
                        bestLen = text.length();
                    }
                }
            }

            if (bestLen < 200) {
                String joined = doc.select("article p, #newsct_article p, #dic_area p, p")
                        .stream()
                        .map(el -> el.text().trim())
                        .filter(s -> s.length() > 10)
                        .collect(Collectors.joining("\n"));

                if (joined.length() > bestLen) {
                    best = joined;
                    bestLen = joined.length();
                }
            }

            if (best.isBlank()) {
                best = doc.text();
            }

            return best
                    .replaceAll("(돌아가기\\s*)+", " ")
                    .replaceAll("\\s{2,}", " ")
                    .trim();

        } catch (Exception e) {
            log.warn("preExtractLikelyBody failed", e);
            return "";
        }
    }
}