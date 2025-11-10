package com.www.viewpoint.assemblymember.service;

import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import com.www.viewpoint.assemblymember.repository.AssemblyMemberRepository;
import com.www.viewpoint.bill.model.dto.BillSummaryDto;
import com.www.viewpoint.bill.repository.BillProposerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssemblyMemberService {

    private final AssemblyMemberRepository assemblyMemberRespotiroy;
    private final BillProposerRepository billProposerRespotiroy;



    public Page<AssemblyMember> getAssemblyMemberAll(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return assemblyMemberRespotiroy.findAll(pageable);
    }

    public Optional<AssemblyMember> getAssemblyMemberById(Long id) {
        return assemblyMemberRespotiroy.findById(id);
    }

    public Page<BillSummaryDto> getBillsByMemberId(Integer memberId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return billProposerRespotiroy.findBillsByMemberId(memberId, pageable);
    }
}
