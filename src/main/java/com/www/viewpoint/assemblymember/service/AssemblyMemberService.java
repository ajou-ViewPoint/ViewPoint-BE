package com.www.viewpoint.assemblymember.service;

import com.www.viewpoint.assemblymember.model.dto.AssemblyMemberDto;
import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import com.www.viewpoint.assemblymember.model.dto.AssemblyMemberQueryProjection;
import com.www.viewpoint.assemblymember.repository.AssemblyMemberRepository;
import com.www.viewpoint.bill.model.dto.BillSummaryDto;
import com.www.viewpoint.bill.repository.BillProposerRepository;
import com.www.viewpoint.share.dto.AssemblyMemberSummaryDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssemblyMemberService {

    private final AssemblyMemberRepository assemblyMemberRespotiroy;
    private final BillProposerRepository billProposerRespotiroy;

    private AssemblyMemberSummaryDto toSummaryDto(AssemblyMemberQueryProjection p) {
        return AssemblyMemberSummaryDto.builder()
                .memberId(p.getMemberId())
                .name(p.getName())
                .party(p.getParty())
                .age(p.getAge())
                .duty(p.getDuty())
                .profileImage(p.getProfileImage())
                .district(p.getDistrict())
                .build();
    }

    public Page<AssemblyMemberSummaryDto> getAssemblyMemberAll(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return assemblyMemberRespotiroy.findAllAssemblyMembers(pageable)
                .map(this::toSummaryDto);

    }

    public AssemblyMemberDto getAssemblyMemberById(Long id) {

        AssemblyMemberQueryProjection am = assemblyMemberRespotiroy.findAssemblyMemberById(id)
                .orElseThrow(() -> new EntityNotFoundException("No AssemblyMember found with id:" + id));
        return AssemblyMemberDto.builder()
                .memberId(am.getMemberId())
                .name(am.getName())
                .party(am.getParty())
                .age(am.getAge())
                .duty(am.getDuty())
                .profileImage(am.getProfileImage())
                .district(am.getDistrict())

                // 상세 필드
                .engName(am.getEngName())
                .chName(am.getChName())
                .birthDate(
                        am.getBirthDate() != null ? am.getBirthDate().toString() : null
                )
                .gender(am.getGender())
                .phone(am.getPhone())
                .innerDuty(am.getInnerDuty())
                .attendanceRate(am.getAttendanceRate())
                .loyaltyRate(am.getLoyaltyRate())
                .history(am.getHistory())
                .build();
    }

    public Page<BillSummaryDto> getBillsByMemberId(Integer memberId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return billProposerRespotiroy.findBillsByMemberId(memberId, pageable);
    }
}
