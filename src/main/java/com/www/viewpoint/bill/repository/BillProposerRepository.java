package com.www.viewpoint.bill.repository;

import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.bill.model.entity.BillProposer;
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
            am.party,
            bp.isRepresentative,
            bp.eraco
        )
        FROM BillProposer bp
        LEFT JOIN AssemblyMember am ON bp.memberId = am.id
        WHERE bp.billId = :billId
        ORDER BY bp.isRepresentative DESC, am.name ASC
    """)
    List<BillProposerMemberDto> findProposersByBillId(@Param("billId") String billId);
}