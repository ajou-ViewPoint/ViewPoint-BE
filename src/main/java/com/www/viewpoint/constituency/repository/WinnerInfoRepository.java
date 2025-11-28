package com.www.viewpoint.constituency.repository;

import com.www.viewpoint.constituency.model.dto.WinnerInfoDto;
import com.www.viewpoint.constituency.model.dto.WinnerInfoProjection;
import com.www.viewpoint.constituency.model.entity.WinnerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WinnerInfoRepository extends JpaRepository<WinnerInfo, Long> {

    @Query(value = """
        SELECT 
           am.id            AS memberId,
           wi.name          AS name,
           p.party_name      AS party,
           wi.eraco           AS age,
           am.duty          AS duty,
           am.profile_image  AS profileImage,
           wi.sgg_name       AS district,
           kd.sido_name      AS sidoName,
           kd.sgg_name       AS sggName,
           kd.code          AS regionCd,
           wi.dugyul        AS voteRate  
        FROM winner_info wi
        LEFT JOIN party p ON wi.party_id = p.id
        LEFT JOIN national_assembly_member am ON wi.member_id = am.id
        LEFT JOIN korea_districts kd ON wi.region_id = kd.id
        WHERE (:sido IS NULL OR kd.sido_name = :sido)
          AND (:sgg IS NULL OR kd.sgg_name = :sgg)
          AND (:code IS NULL OR kd.code = :code)
        ORDER BY wi.eraco DESC
""",
            nativeQuery = true
    )
    List<WinnerInfoProjection> findMembersByRegion(
            @Param("sido") String sido,
            @Param("sgg") String sgg,
            @Param("code") String  code
    );

    @Query(value = """
         SELECT 
           am.id            AS memberId,
           wi.name          AS name,
           p.party_name      AS party,
           wi.eraco           AS age,
           am.eraco         AS eraco,
           am.duty          AS duty,
           am.profile_image  AS profileImage,
           wi.sgg_name       AS district,
           kd.sido_name      AS sidoName,
           kd.sgg_name       AS sggName,
           kd.code          AS regionCd,
           wi.dugyul        AS voteRate         
        FROM winner_info wi
        LEFT JOIN party p ON wi.party_id = p.id
        LEFT JOIN national_assembly_member am ON wi.member_id = am.id
        LEFT JOIN korea_districts kd ON wi.region_id = kd.id
        WHERE wi.region_id = :regionId
        ORDER BY wi.eraco DESC
    """,
            nativeQuery = true
    )
    List<WinnerInfoProjection> findWinnerByRegionId(@Param("regionId") Long regionId);
}