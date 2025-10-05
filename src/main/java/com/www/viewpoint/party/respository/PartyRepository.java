package com.www.viewpoint.party.respository;

import com.www.viewpoint.party.model.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRepository extends JpaRepository<Party, Integer> {
    // 필요하면 커스텀 쿼리 메서드 정의 가능
}

