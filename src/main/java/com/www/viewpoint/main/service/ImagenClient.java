package com.www.viewpoint.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class ImagenClient {

    private final WebClient webClient;

    public ImagenClient(
            @Value("${google.gemini.api-key}") String apiKey,
            WebClient.Builder webClientBuilder
    ) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("x-goog-api-key", apiKey)
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) // 10MB
                )
                .build();
    }

    /**
     * prompt를 넣어서 Imagen 4로 이미지 생성
     */
    public List<GeneratedImage> generateImages(String prompt, int sampleCount) {
        if (prompt == null || prompt.isBlank()) {
            return Collections.emptyList();
        }

        // ✅ 이제는 프롬프트를 자르지 않고 그대로 사용
        ImagenRequest body = new ImagenRequest(
                List.of(new ImagenInstance(prompt)),
                new ImagenParams(sampleCount)
        );

        ImagenResponse response;
        try {
            response = webClient.post()
                    .uri("/v1beta/models/imagen-4.0-generate-001:predict")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(ImagenResponse.class)
                    .block();
        } catch (Exception e) {
            log.warn("Failed to call Imagen API", e);
            return Collections.emptyList();
        }

        if (response == null || response.predictions() == null) {
            log.warn("Imagen response is null or has no predictions");
            return Collections.emptyList();
        }

        return response.predictions().stream()
                .map(p -> new GeneratedImage(
                        p.bytesBase64Encoded(),
                        p.mimeType(),
                        p.prompt()
                ))
                .toList();
    }

    // ====== 아래는 요청/응답 DTO ======

    public record ImagenRequest(
            List<ImagenInstance> instances,
            ImagenParams parameters
    ) {}

    public record ImagenInstance(String prompt) {}

    public record ImagenParams(int sampleCount) {}

    public record ImagenResponse(List<ImagenPrediction> predictions) {}

    public record ImagenPrediction(
            String bytesBase64Encoded,
            String mimeType,
            String prompt // 일부 모델에서만 옴 (enhanced prompt)
    ) {}

    // 서비스에서 재사용할 이미지 DTO (News Article에 포함시킬 용)
    public record GeneratedImage(
            String base64,
            String mimeType,
            String enhancedPrompt
    ) {}

    /**
     * 디버그용: raw JSON을 보고 싶을 때만 사용 (여기는 로그 길이 때문에 800자로 자르는 거 유지해도 됨)
     */
    public String debugRawPredict(String prompt, int sampleCount) {
        if (prompt == null || prompt.isBlank()) {
            return "prompt is blank";
        }

        String trimmed = prompt.length() > 800
                ? prompt.substring(0, 800)
                : prompt;

        ImagenRequest body = new ImagenRequest(
                List.of(new ImagenInstance(trimmed)),
                new ImagenParams(sampleCount)
        );

        return webClient.post()
                .uri("/v1beta/models/imagen-4.0-generate-001:predict")
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .map(b -> "status=" + response.statusCode().value() + "\nbody=" + b)
                )
                .block();
    }
}
