package com.www.viewpoint.constituency.respository;

import com.www.viewpoint.constituency.model.entity.Constituency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstituencyRepository extends JpaRepository<Constituency, Integer> {
    // 필요하면 커스텀 쿼리 메서드 정의 가능
}

