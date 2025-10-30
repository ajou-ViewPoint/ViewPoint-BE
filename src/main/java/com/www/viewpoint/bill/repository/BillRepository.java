package com.www.viewpoint.bill.repository;
import com.www.viewpoint.bill.model.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    // 필요하면 커스텀 쿼리 메서드 정의 가능
    List<Bill> findByProposeDtIsNotNullOrderByProposeDtDescIdDesc();

    List<Bill> findByBillTitleContainingIgnoreCaseOrBillSummaryContainingIgnoreCaseOrProposerContainingIgnoreCase(
            String billTitleKeyword,
            String billSummaryKeyword,
            String proposerKeyword
    );

    List<Bill> findByProposeDtBetween(LocalDate startDate, LocalDate endDate);
}