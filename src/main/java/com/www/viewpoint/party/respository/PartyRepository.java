package com.www.viewpoint.party.respository;

import com.www.viewpoint.party.model.dto.PartyMemberInfoProjection;
import com.www.viewpoint.party.model.dto.PartySeatCountProjection;
import com.www.viewpoint.party.model.entity.Party;
import com.www.viewpoint.party.model.dto.PartySeatStatDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyRepository extends JpaRepository<Party, Integer> {
    // 필요하면 커스텀 쿼리 메서드 정의 가능

    @Query(value = """
        SELECT
            p.id   AS partyId,
            p.party_name AS partyName,
            COUNT(*) AS totalSeats,
            ame.eraco As eraco
        FROM assembly_member_eraco ame
        JOIN party p ON p.id = ame.party_id
        WHERE ame.eraco = :eraco
        GROUP BY p.id, p.party_name
        ORDER BY totalSeats DESC, p.party_name
        """, nativeQuery = true)
    List<PartySeatCountProjection> findPartySeatsByEraco(@Param("eraco") String eraco);

    @Query(value = """
    SELECT
        nam.id AS memberId,
        p.party_name AS partyName,
        nam.duty AS duty,
        nam.name AS name,
        nam.profile_image AS profileImage,
        ae.constituency_type AS constituencyType,
        ae.election_district AS regionName
    FROM national_assembly_member nam
    JOIN assembly_member_eraco ae ON ae.member_id = nam.id
    JOIN party p ON p.id = ae.party_id
    WHERE
        (p.party_name = :partyName
         OR p.id IN (
             SELECT id
             FROM party
             WHERE parent_party_id = (
                 SELECT id FROM party WHERE party_name = :partyName
             )
         ))
        AND ae.eraco = :eraco
    ORDER BY nam.name
    """, nativeQuery = true)
    List<PartyMemberInfoProjection> findAllByPartyAndEracoIncludingSatellite(
            @Param("partyName") String partyName,
            @Param("eraco") String eraco
    );
}

