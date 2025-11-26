package com.www.viewpoint.main.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "topic_news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 뉴스 기준일 (오늘자)
    private LocalDate date;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 생성된 이미지 1장의 base64 (첫 번째 이미지만 저장한다고 가정)
    @Column(columnDefinition = "LONGTEXT")
    private String base64;

    // 원하면 createdAt/updatedAt 추가해도 됨
}
