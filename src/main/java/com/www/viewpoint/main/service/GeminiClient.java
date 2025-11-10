package com.www.viewpoint.main.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GeminiClient {

    private final String apiKey;
    private final String apiBase;
    private final String apiVersion;
    private final String modelPrimary;
    private final String modelFallback;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiClient(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.api-base:https://generativelanguage.googleapis.com}") String apiBase,
            @Value("${gemini.api-version:v1beta}") String apiVersion,
            @Value("${gemini.model-primary:gemini-2.5-pro}") String modelPrimary,
            @Value("${gemini.model-fallback:gemini-2.5-flash}") String modelFallback
    ) {
        this.apiKey = apiKey;
        this.apiBase = apiBase;
        this.apiVersion = apiVersion;
        this.modelPrimary = modelPrimary;
        this.modelFallback = modelFallback;

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(16);
        dispatcher.setMaxRequestsPerHost(8);

        this.httpClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(2, 15, java.util.concurrent.TimeUnit.SECONDS))
                .retryOnConnectionFailure(true)
                .protocols(java.util.List.of(Protocol.HTTP_1_1))
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .readTimeout(java.time.Duration.ofSeconds(60))
                .writeTimeout(java.time.Duration.ofSeconds(60))
                .callTimeout(java.time.Duration.ofSeconds(70))
                .build();

        log.info("[GeminiClient] base={}, ver={}, primary={}, fallback={}",
                apiBase, apiVersion, modelPrimary, modelFallback);
    }

    /**
     * 뉴스 본문 정제 전용 메서드
     * systemPrompt + rawContent를 하나의 입력으로 합쳐 Gemini 호출
     * 실패 시 fallback 모델까지 재시도 후 전체 실패 시 예외 발생
     */
    public String extractArticlePlain(String systemPrompt, String rawContent) throws Exception {
        String merged = (systemPrompt == null ? "" : systemPrompt.trim())
                + "\n\n---\n\n"
                + (rawContent == null ? "" : rawContent.trim());

        if (merged.isBlank()) {
            return "";
        }

        try {
            return callGenerateContentWithRetry(modelPrimary, merged);
        } catch (RuntimeException primaryError) {
            try {
                return callGenerateContentWithRetry(modelFallback, merged);
            } catch (RuntimeException fallbackError) {
                throw new RuntimeException(
                        "Gemini both calls failed. primary=" + primaryError.getMessage()
                                + ", fallback=" + fallbackError.getMessage()
                );
            }
        }
    }

    /* ========================= 내부 공통 요청 ========================= */

    private String callGenerateContentWithRetry(String model, String text) throws Exception {
        Request request = buildGenerateContentRequest(model, text);

        try (Response response = executeWithRobustRetry(request)) {
            String body = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP " + response.code() + " - " + body);
            }
            return extractText(body);
        }
    }

    private Request buildGenerateContentRequest(String model, String text) {
        String url = String.format("%s/%s/models/%s:generateContent?key=%s",
                apiBase, apiVersion, model, apiKey);

        String payload = """
                {
                  "contents": [
                    { "role": "user", "parts": [ { "text": %s } ] }
                  ]
                }
                """.formatted(jsonEscape(text));

        return new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Connection", "close") // 일부 환경에서 write 오류 완화
                .post(RequestBody.create(
                        payload,
                        MediaType.parse("application/json")
                ))
                .build();
    }

    private Response executeWithRobustRetry(Request request) throws Exception {
        final int maxAttempts = 7;
        final long baseMs = 500L;
        final long maxSleepMs = 20_000L;
        final long totalBudgetMs = 70_000L;

        long started = System.currentTimeMillis();
        java.util.Random rnd = new java.util.Random();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                Response response = httpClient.newCall(request).execute();
                int code = response.code();

                if (code == 429 || code == 503) {
                    String retryAfter = response.header("Retry-After");
                    response.close();

                    long sleepMs = parseRetryAfterMs(retryAfter, baseMs, attempt, rnd, maxSleepMs);
                    if (System.currentTimeMillis() - started + sleepMs > totalBudgetMs) break;

                    Thread.sleep(sleepMs);
                    continue;
                }

                if (isTransientHttp(code)) {
                    String body = response.body() != null ? response.body().string() : "";
                    response.close();

                    long sleepMs = jitter(baseMs, attempt, rnd, maxSleepMs);
                    if (System.currentTimeMillis() - started + sleepMs > totalBudgetMs) break;

                    log.debug("Transient HTTP {}. Retry in {} ms body={}", code, sleepMs, body);
                    Thread.sleep(sleepMs);
                    continue;
                }

                return response;
            } catch (java.net.SocketTimeoutException | java.net.ConnectException e) {
                if (attempt == maxAttempts) throw e;

                long sleepMs = jitter(baseMs, attempt, rnd, maxSleepMs);
                if (System.currentTimeMillis() - started + sleepMs > totalBudgetMs) break;

                log.debug("Network error {}. Retry in {} ms", e.getClass().getSimpleName(), sleepMs);
                Thread.sleep(sleepMs);
            } catch (javax.net.ssl.SSLException e) {
                if (attempt >= Math.min(3, maxAttempts)) throw e;

                long sleepMs = jitter(baseMs, attempt, rnd, maxSleepMs);
                Thread.sleep(sleepMs);
            }
        }

        throw new RuntimeException("Too Many Retries");
    }

    private boolean isTransientHttp(int code) {
        return code == 408 || code == 425 || code == 500 || code == 502 || code == 503 || code == 504;
    }

    private long parseRetryAfterMs(String retryAfter, long baseMs, int attempt,
                                   java.util.Random rnd, long capMs) {
        if (retryAfter != null && retryAfter.matches("\\d+")) {
            long ms = Long.parseLong(retryAfter) * 1000L;
            return Math.min(ms, capMs);
        }
        return jitter(baseMs, attempt, rnd, capMs);
    }

    private long jitter(long baseMs, int attempt, java.util.Random rnd, long capMs) {
        long exp = (long) (baseMs * Math.pow(2, attempt));
        long withJitter = (long) (rnd.nextDouble() * exp);
        return Math.min(withJitter, capMs);
    }

    private String extractText(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode candidates = root.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (JsonNode part : candidates.get(0).path("content").path("parts")) {
            String t = part.path("text").asText(null);
            if (t != null) sb.append(t);
        }
        return sb.toString().trim();
    }

    private static String jsonEscape(String s) {
        if (s == null) return "\"\"";
        return "\"" + s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                + "\"";
    }
}