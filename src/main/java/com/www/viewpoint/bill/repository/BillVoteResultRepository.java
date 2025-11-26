package com.www.viewpoint.bill.repository;

import com.www.viewpoint.bill.model.dto.VoteSummaryProjection;
import com.www.viewpoint.bill.model.entity.BillVoteResult;
import com.www.viewpoint.bill.model.entity.BillVoteResultId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillVoteResultRepository
        extends JpaRepository<BillVoteResult, BillVoteResultId> {

    // 특정 법안 투표 전체 조회
    List<BillVoteResult> findByIdBillId(String billId);

    @Query(
            value = """
        SELECT 
            n.id AS id,
            n.name AS name,
            le.age  AS age,
            n.duty AS duty,
            le.party_name AS partyName,
            le.election_district AS electionDistrict,
            n.profile_image AS profileImage,
            v.vote_opinion AS voteOpinion
        FROM vote_bill_nass v
        JOIN national_assembly_member n
            ON v.nass_id = n.id
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
        WHERE v.bill_id = :billId;
    """,
            nativeQuery = true
    )
    List<VoteSummaryProjection> findVoteSummary(@Param("billId") String billId);
}