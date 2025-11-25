package com.www.viewpoint.main.repository;

import com.www.viewpoint.main.model.entity.TopicNews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TopicNewsRepository extends JpaRepository<TopicNews, Long> {

    Optional<TopicNews> findFirstByDateOrderByIdDesc(LocalDate date);
}