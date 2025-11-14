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
            kd.sidoName,
            kd.sggName,
            kd.code,
            wi.dugyul,
            p.partyName,
            am.id,
            am.profileImage
       )
    FROM WinnerInfo wi
    LEFT JOIN Party p ON wi.partyId = p.id
    LEFT JOIN AssemblyMember am ON wi.memberId = am.id
    LEFT JOIN KoreaDistrict  kd ON wi.regionId = kd.id
    WHERE (:sido IS NULL OR kd.sidoName = :sido)
      AND (:sgg IS NULL OR kd.sggName = :sgg)
      AND (:code IS NULL OR kd.code = :code)
    ORDER BY wi.eraco DESC
""")
    List<WinnerInfoDto> findMembersByRegion(
            @Param("sido") String sido,
            @Param("sgg") String sgg,
            @Param("code") String  code
    );

    @Query("""
        SELECT new com.www.viewpoint.constituency.model.dto.WinnerInfoDto(
            wi.eraco,
            wi.name,
            wi.jdName,
            wi.sggName,
            kr.sidoName,
            kr.sggName,
            kr.code,
            wi.dugyul,
            p.partyName,
            am.id,
            am.profileImage
        )
        FROM WinnerInfo wi
        LEFT JOIN Party p ON wi.partyId = p.id
        LEFT JOIN AssemblyMember am ON wi.memberId = am.id
        LEFT JOIN KoreaDistrict  kr ON wi.regionId =kr.id
        WHERE wi.regionId = :regionId
        ORDER BY wi.eraco DESC
    """)
    List<WinnerInfoDto> findWinnerByRegionId(@Param("regionId") Long regionId);
}