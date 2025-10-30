package com.www.viewpoint.committee.repository;

import com.www.viewpoint.committee.model.entity.Committee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeRepository extends JpaRepository<Committee, Integer> {
    // 필요하면 커스텀 쿼리 메서드 정의 가능
}

