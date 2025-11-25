package com.www.viewpoint.main.service;

import com.www.viewpoint.main.model.entity.TopicNews;
import com.www.viewpoint.main.repository.TopicNewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicNewsService {

    private final TopicNewsRepository topicNewsRepository;

    private LocalDate todayKst() {
        return LocalDate.now(ZoneId.of("Asia/Seoul"));
    }

    /**
     * 오늘자 TopicNews 조회
     */
    public Optional<TopicNews> findTodayNews() {
        return topicNewsRepository.findFirstByDateOrderByIdDesc(todayKst());
    }

    /**
     * Article 결과를 오늘 날짜로 저장/업데이트
     */
    public TopicNews saveTodayFromArticle(GoogleNewsCrawlerService.Article article) {
        LocalDate today = todayKst();

        String base64 = null;
        if (article.images() != null && !article.images().isEmpty()) {
            base64 = article.images().getFirst().base64(); // 첫 번째 이미지만 저장한다고 가정
        }

        TopicNews entity = topicNewsRepository
                .findFirstByDateOrderByIdDesc(today)
                .orElseGet(TopicNews::new);

        entity.setDate(today);
        entity.setTitle(article.title());
        entity.setContent(article.content());
        entity.setBase64(base64);

        return topicNewsRepository.save(entity);
    }

    /**
     * TopicNews 엔티티를 /top-google에서 쓰는 Article 형태로 변환
     */
    public GoogleNewsCrawlerService.Article toArticleFromEntity(TopicNews entity) {

        List<ImagenClient.GeneratedImage> images;

        if (entity.getBase64() != null && !entity.getBase64().isBlank()) {
            // mimeType은 Imagen에서 대부분 image/png일 테니 기본값 가정
            images = List.of(new ImagenClient.GeneratedImage(
                    entity.getBase64(),
                    "image/png",
                    null
            ));
        } else {
            images = Collections.emptyList();
        }

        return new GoogleNewsCrawlerService.Article(
                entity.getTitle(),
                null,               // link는 저장 컬럼이 없으니 null/혹은 "" 처리
                null,               // source도 동일
                entity.getContent(),
                images
        );
    }
}