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
            COALESCE(n.name, v.name) AS name,
            COALESCE(n.age, v.age) AS age,
            n.duty AS duty,
            COALESCE(n.party, v.party_name) AS partyName,
            n.profile_image AS profileImage,
            v.vote_opinion AS voteOpinion
        FROM vote_bill_nass v
        LEFT JOIN national_assembly_member n
            ON v.nass_id = n.id
        WHERE v.bill_id = :billId
        """,
            nativeQuery = true
    )
    List<VoteSummaryProjection> findVoteSummary(@Param("billId") String billId);
}