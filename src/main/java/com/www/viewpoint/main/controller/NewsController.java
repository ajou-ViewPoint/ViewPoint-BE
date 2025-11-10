package com.www.viewpoint.main.controller;

import com.www.viewpoint.main.service.GoogleNewsCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {

    private final GoogleNewsCrawlerService googleNewsCrawlerService;

    @GetMapping("/top-google")
    public ResponseEntity<?> topGoogle() {
        var article = googleNewsCrawlerService.getTopNewsWithGemini();

        if (article == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "failed_to_fetch_news"));
        }

        // Article 레코드는 Jackson으로 직렬화 가능하므로 그대로 반환
        return ResponseEntity.ok(article);
    }
}