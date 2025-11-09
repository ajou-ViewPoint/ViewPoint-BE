package com.www.viewpoint.bill.repository;

import com.www.viewpoint.bill.model.dto.BillSummaryDto;
import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.bill.model.entity.BillProposer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import com.www.viewpoint.bill.model.dto.BillProposerMemberDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillProposerRepository extends JpaRepository<BillProposer, Integer> {

        @Query("""
                SELECT new com.www.viewpoint.bill.model.dto.BillProposerMemberDto(
                       bp.memberId,
                       am.name,
                       am.profileImage,
                       p.partyName,
                       bp.isRepresentative,
                       bp.eraco
                   )
                   FROM BillProposer bp
                   JOIN Bill b ON bp.billId = b.billId
                   LEFT JOIN AssemblyMember am ON bp.memberId = am.id
                   LEFT JOIN AssemblyMemberEraco ame ON ame.memberId = bp.memberId AND ame.eraco = bp.eraco
                   LEFT JOIN Party p ON ame.partyId = p.id
                   WHERE b.id = :billDbId
                   ORDER BY bp.isRepresentative DESC, am.name ASC
        """
        )
    List<BillProposerMemberDto> findProposersByBillDbId(@Param("billDbId") Long billDbId);

    @Query("""
        SELECT new com.www.viewpoint.bill.model.dto.BillSummaryDto(
            b.id,
            b.billId,
            b.billTitle,
            bp.isRepresentative,
            bp.eraco
        )
        FROM BillProposer bp
        JOIN Bill b ON bp.billId = b.billId
        WHERE bp.memberId = :memberId
        ORDER BY b.proposeDt DESC
    """)
    Page<BillSummaryDto> findBillsByMemberId(
            @Param("memberId") Integer memberId,
            Pageable pageable
    );

}