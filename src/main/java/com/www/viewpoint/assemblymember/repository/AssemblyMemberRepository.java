package com.www.viewpoint.assemblymember.repository;

import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import com.www.viewpoint.main.dto.MemberSimpleDto;
import com.www.viewpoint.main.repository.MemberSimpleProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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
}
