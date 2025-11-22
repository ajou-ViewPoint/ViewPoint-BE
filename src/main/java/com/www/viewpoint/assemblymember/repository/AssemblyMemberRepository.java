package com.www.viewpoint.assemblymember.repository;

import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import com.www.viewpoint.assemblymember.model.dto.AssemblyMemberQueryProjection;
import com.www.viewpoint.main.repository.MemberSimpleProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssemblyMemberRepository extends JpaRepository<AssemblyMember, Long> {
    @Query(
            value = "SELECT * FROM national_assembly_member WHERE age = ?1 ORDER BY RAND() LIMIT 8",
            nativeQuery = true
    )
    List<AssemblyMember> findRandomByAgeLimit8(Integer age);

    @Query(
            value = "SELECT * FROM national_assembly_member ORDER BY RAND() LIMIT 8",
            nativeQuery = true
    )
    List<AssemblyMember> findRandomLimit8();

    @Query(value = """
        SELECT 
            m.naas_code       AS naasCode,
            m.name            AS name,
            m.profile_image   AS profileImage,
            ame.election_district AS district
        FROM national_assembly_member m
        LEFT JOIN assembly_member_eraco ame
          ON ame.member_id = m.id
         AND ame.eraco = m.eraco   -- 현재 회기 기준으로 매칭 (상황에 따라 조정 가능)
        WHERE m.age = ?1
        ORDER BY RAND()
        LIMIT 8
        """, nativeQuery = true)
    List<MemberSimpleProjection> findRandomByAgeLimit8WithDistrict(Integer age);

    @Query(value = """
        SELECT 
            m.naas_code       AS naasCode,
            m.name            AS name,
            m.profile_image   AS profileImage,
            ame.election_district AS district
        FROM national_assembly_member m
        LEFT JOIN assembly_member_eraco ame
          ON ame.member_id = m.id
         AND ame.eraco = m.eraco
        ORDER BY RAND()
        LIMIT 8
        """, nativeQuery = true)
    List<MemberSimpleProjection> findRandomLimit8WithDistrict();


    @Query(value = """
        SELECT
            n.id AS memberId,
            n.name AS name,
            le.party_name AS party,
            le.age AS age,
            n.duty AS duty,
            n.profile_image AS profileImage,
            le.election_district AS district,
            -- AssemblyMemberDto 필드들
            n.eng_name AS engName,
            n.ch_name AS chName,
            n.birth_date AS birthDate,
            n.gender AS gender,
            n.phone AS phone,
            n.inner_duty AS innerDuty,
            n.attendance_rate AS attendanceRate,
            n.loyalty_rate AS loyaltyRate,
            n.history AS history
        FROM national_assembly_member n
        LEFT JOIN (
            SELECT
                t.member_id,
                t.age,
                t.election_district,
                t.party_name
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
        WHERE n.id = :id
        """,
            nativeQuery = true)
    Optional<AssemblyMemberQueryProjection> findAssemblyMemberById(@Param("id") Long id);
}
