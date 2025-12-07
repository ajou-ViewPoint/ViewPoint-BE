package com.www.viewpoint.assemblymember.service;

import com.www.viewpoint.assemblymember.model.dto.AssemblyMemberDto;
import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import com.www.viewpoint.assemblymember.model.dto.AssemblyMemberQueryProjection;
import com.www.viewpoint.assemblymember.model.entity.AssemblyMemberEraco;
import com.www.viewpoint.assemblymember.repository.AssemblyMemberRepository;
import com.www.viewpoint.bill.model.dto.BillSummaryDto;
import com.www.viewpoint.bill.repository.BillProposerRepository;
import com.www.viewpoint.committee.model.entity.Committee;
import com.www.viewpoint.share.dto.AssemblyMemberSummaryDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssemblyMemberService {

    private final AssemblyMemberRepository assemblyMemberRepository;
    private final BillProposerRepository billProposerRepository;

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
        return assemblyMemberRepository.findAllAssemblyMember(pageable)
                .map(this::toSummaryDto);
    }


    @Transactional(readOnly = true)
    public AssemblyMemberDto getAssemblyMemberById(Long id) {

        log.info("[getAssemblyMemberById] start id={}", id);

        AssemblyMember am = assemblyMemberRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[getAssemblyMemberById] AssemblyMember not found id={}", id);
                    return new EntityNotFoundException("No AssemblyMember found with id:" + id);
                });

        // 기본 필드 로그
        log.info("[getAssemblyMemberById] loaded id={} name={} birthDate={}",
                am.getId(), am.getName(), am.getBirthDate());

        // 컬렉션 필드 null 여부와 크기 로그
        log.info("[getAssemblyMemberById] eracos null? {} size={}",
                (am.getEracos() == null),
                (am.getEracos() != null ? am.getEracos().size() : null));

        log.info("[getAssemblyMemberById] committees null? {} size={}",
                (am.getCommittees() == null),
                (am.getCommittees() != null ? am.getCommittees().size() : null));

        // ====== 안전하게 null 방어 후 변환 ======

        // eracos null 방어
        List<AssemblyMemberEraco> eracos =
                am.getEracos() != null ? am.getEracos() : List.of();

        List<String> eracoList = eracos.stream()
                .map(AssemblyMemberEraco::getEraco)
                .toList();

        List<String> districtList = eracos.stream()
                .map(AssemblyMemberEraco::getElectionDistrict)
                .toList();

        List<String> partyList = eracos.stream()
                .map(e -> e.getParty() != null ? e.getParty().getPartyName() : null)
                .toList();

        // committees null 방어
        List<Committee> committees =
                am.getCommittees() != null ? am.getCommittees() : List.of();

        // birthDate null 방어
        String birthDateStr = am.getBirthDate() != null
                ? am.getBirthDate().toString()
                : null;

        return AssemblyMemberDto.builder()
                .memberId(am.getId().longValue())
                .name(am.getName())
                .profileImage(am.getProfileImage())
                .engName(am.getEngName())
                .chName(am.getChName())
                .eraco(eracoList)
                .electionDistrict(districtList)
                .parties(partyList)
                .history(am.getHistory())
                .committees(committees)
                .phone(am.getPhone())
                .gender(am.getGender())
                .birthDate(birthDateStr)
                .innerDuty(am.getInnerDuty())
                .build();
    }

    public Page<BillSummaryDto> getBillsByMemberId(Integer memberId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return billProposerRepository.findBillsByMemberId(memberId, pageable);
    }

    private AssemblyMemberDto toDto(AssemblyMemberQueryProjection am) {
        return AssemblyMemberDto.builder()
                .memberId(am.getMemberId())
                .name(am.getName())
                .duty(am.getDuty())
                .profileImage(am.getProfileImage())
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

    public Page<AssemblyMemberDto> filterAssemblyMembers(
            String keyword,
            String eraco,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<AssemblyMemberQueryProjection> resultPage =
                assemblyMemberRepository.searchMembers(keyword, eraco, pageable);

        return resultPage.map(this::toDto);
    }
}
