package com.www.viewpoint.committee.repository;

import com.www.viewpoint.committee.dto.MemberWithRole;
import com.www.viewpoint.committee.dto.MemberWithRoleProjection;
import com.www.viewpoint.committee.model.entity.Committee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

public interface CommitteeRepository extends JpaRepository<Committee, Integer> {

    Optional<Committee> findByCommitteeName(String committeeName);
    List<Committee> findByCommitteeNameContainingIgnoreCase(String committeeName);

    // 1) 위원회별 의원 상세 (역할 포함)
    @Query(value = """
    SELECT 
        n.id AS memberId,
        nc.role AS role,
        n.name AS name,
        le.age AS age,
        n.duty AS duty,
        le.party_name AS partyName,
        le.election_district AS electionDistrict,
        n.profile_image AS profileImage
    FROM naas_committee nc
    LEFT JOIN national_assembly_member n
        ON nc.member_id = n.id
    LEFT JOIN (
        SELECT *
        FROM (
            SELECT
                ame.member_id,
                ame.age,
                ame.election_district,
                p.party_name AS party_name,
                ROW_NUMBER() OVER (
                    PARTITION BY ame.member_id
                    ORDER BY ame.age DESC
                ) AS rn
            FROM assembly_member_eraco ame
            LEFT JOIN party p
                ON ame.party_id = p.id
        ) t
        WHERE t.rn = 1
    ) le
        ON le.member_id = n.id
    WHERE nc.committee_id = :committeeId
    """,
            nativeQuery = true)
    List<MemberWithRoleProjection> findMembersByCommitteeId(Integer committeeId);

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
