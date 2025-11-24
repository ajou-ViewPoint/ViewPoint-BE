package com.www.viewpoint.bill.repository;
import com.www.viewpoint.bill.model.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer>, JpaSpecificationExecutor<Bill> {

    List<Bill> findTop3ByProposeDtIsNotNullOrderByProposeDtDescIdDesc();

    List<Bill> findByProposeDtIsNotNullOrderByProposeDtDescIdDesc();

    List<Bill> findByBillTitleContainingIgnoreCaseOrBillSummaryContainingIgnoreCaseOrProposerContainingIgnoreCase(
            String billTitleKeyword,
            String billSummaryKeyword,
            String proposerKeyword
    );

    List<Bill> findByProposeDtBetween(LocalDate startDate, LocalDate endDate);

    Bill findByBillId(String billId);

    @Query("""
    SELECT b
    FROM Bill b
    WHERE b.rgsProcDate IS NOT NULL
    ORDER BY b.rgsProcDate DESC, b.id DESC
""")
    List<Bill> findTop3ByRgsProcDateDesc(Pageable pageable);

    Page<Bill> findByBillTitleContainingIgnoreCaseOrBillSummaryContainingIgnoreCaseOrProposerContainingIgnoreCase(
            String billTitleKeyword,
            String billSummaryKeyword,
            String proposerKeyword,
            Pageable pageable
    );
}