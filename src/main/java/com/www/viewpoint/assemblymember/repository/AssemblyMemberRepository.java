package com.www.viewpoint.assemblymember.repository;

import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import com.www.viewpoint.assemblymember.model.dto.AssemblyMemberQueryProjection;
import com.www.viewpoint.main.repository.MemberSimpleProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            WHERE t.age = :age
        ) le
            ON le.member_id = n.id
        WHERE le.age IS NOT NULL
        ORDER BY RAND()
        LIMIT 8
        """, nativeQuery = true)
    List<AssemblyMemberQueryProjection> findRandomByAgeLimit8WithDistrict(Integer age);

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
        ORDER BY RAND()
        LIMIT 8
        """, nativeQuery = true)
    List<AssemblyMemberQueryProjection> findRandomLimit8WithDistrict();


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
        """,
            countQuery = """
        SELECT count(*)
        FROM national_assembly_member n
    """,
        nativeQuery = true)
    Page<AssemblyMemberQueryProjection> findAllAssemblyMember(Pageable pageable);

    @Query(
            value = """
        SELECT
            n.id AS memberId,
            n.name AS name,
            le.party_name AS party,
            le.age AS age,
            n.duty AS duty,
            n.profile_image AS profileImage,
            le.election_district AS district,
            -- 상세 필드
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
                ame.member_id,
                ame.age,
                ame.election_district,
                ame.eraco,
                p.party_name AS party_name,
                ROW_NUMBER() OVER (
                    PARTITION BY ame.member_id
                    ORDER BY ame.age DESC
                ) AS rn
            FROM assembly_member_eraco ame
            LEFT JOIN party p
                ON ame.party_id = p.id
        ) le
            ON le.member_id = n.id
           AND le.rn = 1
        WHERE 1 = 1
          -- 🔍 검색어 필터: 이름 / 정당 / 지역구
          AND (
              :keyword IS NULL OR :keyword = '' OR
              n.name LIKE CONCAT('%', :keyword, '%') OR
              le.party_name LIKE CONCAT('%', :keyword, '%') OR
              le.election_district LIKE CONCAT('%', :keyword, '%')
          )
          -- 🏛 재직 대수 필터: assembly_member_eraco.eraco 기준 (예: '제22대')
          AND (
              :eraco IS NULL OR :eraco = '' OR
              le.eraco = :eraco
          )
        """,
            countQuery = """
        SELECT
            COUNT(*)
        FROM national_assembly_member n
        LEFT JOIN (
            SELECT
                ame.member_id,
                ame.age,
                ame.election_district,
                ame.eraco,
                p.party_name AS party_name,
                ROW_NUMBER() OVER (
                    PARTITION BY ame.member_id
                    ORDER BY ame.age DESC
                ) AS rn
            FROM assembly_member_eraco ame
            LEFT JOIN party p
                ON ame.party_id = p.id
        ) le
            ON le.member_id = n.id
           AND le.rn = 1
        WHERE 1 = 1
          AND (
              :keyword IS NULL OR :keyword = '' OR
              n.name LIKE CONCAT('%', :keyword, '%') OR
              le.party_name LIKE CONCAT('%', :keyword, '%') OR
              le.election_district LIKE CONCAT('%', :keyword, '%')
          )
          AND (
              :eraco IS NULL OR :eraco = '' OR
              le.eraco = :eraco
          )
        """,
            nativeQuery = true
    )
    Page<AssemblyMemberQueryProjection> searchMembers(
            @Param("keyword") String keyword,
            @Param("eraco") String eraco,
            Pageable pageable
    );

    @Query(
            value = """
        SELECT
            n.id AS memberId,
            n.name AS name,
            le.party_name AS party,
            le.age AS age,
            n.duty AS duty,
            n.profile_image AS profileImage,
            le.election_district AS district,
            -- 상세 필드
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
        INNER JOIN (
            SELECT
                t.member_id,
                t.age,
                t.election_district,
                t.eraco,
                t.party_name
            FROM (
                SELECT
                    ame.member_id,
                    ame.age,
                    ame.election_district,
                    ame.eraco,
                    p.party_name AS party_name,
                    ROW_NUMBER() OVER (
                        PARTITION BY ame.member_id
                        ORDER BY ame.age DESC
                    ) AS rn
                FROM assembly_member_eraco ame
                LEFT JOIN party p
                    ON ame.party_id = p.id
                WHERE 1 = 1
                  -- 🏛 재직 대수 필터: assembly_member_eraco.eraco 기준
                  AND (
                      :eraco IS NULL OR :eraco = '' OR
                      ame.eraco = :eraco
                  )
                  -- 🎯 정당 필터: party.party_name 기준
                  AND (
                      :party IS NULL OR :party = '' OR
                      p.party_name = :party
                  )
            ) t
            WHERE t.rn = 1
        ) le
            ON le.member_id = n.id
        WHERE 1 = 1
          -- 🔍 검색어 필터: 이름만 (keyword는 이름으로만 검색)
          AND (
              :keyword IS NULL OR :keyword = '' OR
              n.name LIKE CONCAT('%', :keyword, '%')
          )
        """,
            countQuery = """
        SELECT
            COUNT(DISTINCT n.id)
        FROM national_assembly_member n
        INNER JOIN (
            SELECT
                t.member_id
            FROM (
                SELECT
                    ame.member_id,
                    ROW_NUMBER() OVER (
                        PARTITION BY ame.member_id
                        ORDER BY ame.age DESC
                    ) AS rn
                FROM assembly_member_eraco ame
                LEFT JOIN party p
                    ON ame.party_id = p.id
                WHERE 1 = 1
                  AND (
                      :eraco IS NULL OR :eraco = '' OR
                      ame.eraco = :eraco
                  )
                  AND (
                      :party IS NULL OR :party = '' OR
                      p.party_name = :party
                  )
            ) t
            WHERE t.rn = 1
        ) le
            ON le.member_id = n.id
        WHERE 1 = 1
          AND (
              :keyword IS NULL OR :keyword = '' OR
              n.name LIKE CONCAT('%', :keyword, '%')
          )
        """,
            nativeQuery = true
    )
    Page<AssemblyMemberQueryProjection> filterMembersByEraco(
            @Param("keyword") String keyword,
            @Param("eraco") String eraco,
            @Param("party") String party,
            Pageable pageable
    );


}
