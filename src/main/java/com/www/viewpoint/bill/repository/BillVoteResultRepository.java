package com.www.viewpoint.bill.repository;

import com.www.viewpoint.bill.model.entity.BillVoteResult;
import com.www.viewpoint.bill.model.entity.BillVoteResultId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillVoteResultRepository
        extends JpaRepository<BillVoteResult, BillVoteResultId> {

    // 특정 법안 투표 전체 조회
    List<BillVoteResult> findByIdBillId(String billId);

    // 특정 법안 + 특정 의견(찬성/반대/기권/불참)
    List<BillVoteResult> findByIdBillIdAndVoteOpinion(String billId, String voteOpinion);
}