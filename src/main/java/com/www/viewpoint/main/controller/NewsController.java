package com.www.viewpoint.main.controller;

import com.www.viewpoint.main.service.GoogleNewsCrawlerService;
import com.www.viewpoint.main.service.TopicNewsService;
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
    private final TopicNewsService topicNewsService;

    @GetMapping("/top-google")
    public ResponseEntity<?> topGoogle() {

        // 1) 오늘자 DB에 있으면 그걸 Article로 변환해서 반환 (크롤 X)
        var todayEntityOpt = topicNewsService.findTodayNews();
        if (todayEntityOpt.isPresent()) {
            var articleFromDb = topicNewsService.toArticleFromEntity(todayEntityOpt.get());
            return ResponseEntity.ok(articleFromDb);
        }

        // 2) 없으면 실제 크롤 + Gemini + Imagen 호출
        var article = googleNewsCrawlerService.getTopNewsWithGemini();

        if (article == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "failed_to_fetch_news"));
        }

        // 3) 얻은 결과를 DB에도 저장
        topicNewsService.saveTodayFromArticle(article);

        // 4) 그리고 그대로 반환
        return ResponseEntity.ok(article);
    }
}