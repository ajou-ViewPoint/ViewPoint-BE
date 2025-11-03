package com.www.viewpoint.constituency.repository;

import com.www.viewpoint.constituency.model.dto.WinnerInfoDto;
import com.www.viewpoint.constituency.model.entity.WinnerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WinnerInfoRepository extends JpaRepository<WinnerInfo, Long> {

    @Query("""
        SELECT new com.www.viewpoint.constituency.model.dto.WinnerInfoDto(
            wi.eraco,
            wi.name,
            wi.jdName,
            wi.sggName,
            wi.sdName,
            wi.wiwName,
            wi.dugyul,
            p.partyName,
            am.id,
            am.profileImage
        )
        FROM WinnerInfo wi
        LEFT JOIN Party p ON wi.partyId = p.id
        LEFT JOIN AssemblyMember am ON wi.memberId = am.id
        WHERE (:sido IS NULL OR wi.sdName LIKE CONCAT('%', :sido, '%'))
          AND (:gungu IS NULL OR wi.wiwName LIKE CONCAT('%', :gungu, '%'))
          AND (:eracos IS NULL OR wi.eraco = :eracos)
        ORDER BY wi.eraco DESC
    """)
    List<WinnerInfoDto> findMembersByRegion(
            @Param("sido") String sido,
            @Param("gungu") String gungu,
            @Param("eracos") String eracos
    );
}