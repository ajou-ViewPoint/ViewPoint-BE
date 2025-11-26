package com.www.viewpoint.main.scheduler;

import com.www.viewpoint.main.service.GoogleNewsCrawlerService;
import com.www.viewpoint.main.service.TopicNewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicNewsScheduler {

    private final GoogleNewsCrawlerService googleNewsCrawlerService;
    private final TopicNewsService topicNewsService;


    @Scheduled(cron = "0 0 00 * * ?", zone = "Asia/Seoul")
    public void fetchAndStoreDailyTopNews() {
        log.info("[TopicNewsScheduler] Running daily top news job at 23:00 KST");

        try {
            var article = googleNewsCrawlerService.getTopNewsWithGemini();
            if (article == null) {
                log.warn("[TopicNewsScheduler] googleNewsCrawlerService returned null article");
                return;
            }

            topicNewsService.saveTodayFromArticle(article);
            log.info("[TopicNewsScheduler] Saved today's topic_news successfully");

        } catch (Exception e) {
            log.error("[TopicNewsScheduler] Failed to fetch/store daily top news", e);
        }
    }
}
