package com.www.viewpoint.committee.repository;

import com.www.viewpoint.committee.dto.MemberWithRole;
import com.www.viewpoint.committee.model.entity.Committee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

public interface CommitteeRepository extends JpaRepository<Committee, Integer> {

    Optional<Committee> findByCommitteeName(String committeeName);

    // 1) 위원회별 의원 상세 (역할 포함)
    @Query(value = """
        SELECT 
            m.id            AS memberId,
            nc.role         AS role,
            m.name          AS name,
            m.party         AS party,
            m.profile_image AS profileImage,
            m.naas_code     AS naasCode,
            m.age           AS age
        FROM naas_committee nc
        JOIN national_assembly_member m
          ON nc.member_id = m.id
        WHERE nc.committee_id = :committeeId
        """,
            nativeQuery = true)
    List<MemberWithRole> findMembersByCommitteeId(Integer committeeId);

    // 2) 정당별 인원수 집계
    @Query(value = """
        SELECT 
            m.party   AS party,
            COUNT(*)  AS cnt
        FROM naas_committee nc
        JOIN national_assembly_member m
          ON nc.member_id = m.id
        WHERE nc.committee_id = :committeeId
        GROUP BY m.party
        """,
            nativeQuery = true)
    List<Object[]> countPartyDistribution(Integer committeeId);
}
