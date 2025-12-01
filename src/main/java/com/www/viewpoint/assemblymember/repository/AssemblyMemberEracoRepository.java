package com.www.viewpoint.assemblymember.repository;

import com.www.viewpoint.assemblymember.model.entity.AssemblyMemberEraco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssemblyMemberEracoRepository extends JpaRepository<AssemblyMemberEraco, Integer> {

    // 🔥 특정 대수만 조회
    Page<AssemblyMemberEraco> findByEraco(String eraco, Pageable pageable);

    // 🔥 대수 + 정당 + 지역구 등 복합 조건도 가능 (확장 용도)
    Page<AssemblyMemberEraco> findByEracoAndPartyId(String eraco, Integer partyId, Pageable pageable);
}