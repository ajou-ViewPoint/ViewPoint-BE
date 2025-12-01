package com.www.viewpoint.Rdata.repository;

import com.www.viewpoint.Rdata.model.Nominate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NominateRepository extends JpaRepository<Nominate, Integer> {

    @Query(value = """
        SELECT 
            m.name        AS memberName,
            n.x           AS x,
            n.y           AS y,
            p.party_name  AS partyName
        FROM nominate n
        LEFT JOIN national_assembly_member m
            ON m.naas_code = n.naas_code   -- 여기만 실제 컬럼명에 맞게 수정
        LEFT JOIN party p
            ON p.id = n.party_id
        WHERE n.age = :age
          AND n.x IS NOT NULL
          AND n.y IS NOT NULL
        """,
            nativeQuery = true
    )
    List<NominateProjection> findByAge(@Param("age") Integer age);
}
