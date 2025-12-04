package com.www.viewpoint.assemblymember.service;

import com.www.viewpoint.assemblymember.model.dto.AssemblyMemberDto;
import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import com.www.viewpoint.assemblymember.model.dto.AssemblyMemberQueryProjection;
import com.www.viewpoint.assemblymember.model.entity.AssemblyMemberEraco;
import com.www.viewpoint.assemblymember.repository.AssemblyMemberEracoRepository;
import com.www.viewpoint.assemblymember.repository.AssemblyMemberRepository;
import com.www.viewpoint.bill.model.dto.BillSummaryDto;
import com.www.viewpoint.bill.repository.BillProposerRepository;
import com.www.viewpoint.committee.dto.CommitteeDto;
import com.www.viewpoint.committee.model.entity.Committee;
import com.www.viewpoint.share.dto.AssemblyMemberSummaryDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssemblyMemberService {

    private final AssemblyMemberRepository assemblyMemberRespotiroy;
    private final BillProposerRepository billProposerRespotiroy;
    private final AssemblyMemberEracoRepository eracoRepository;

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
    private AssemblyMemberSummaryDto toSummaryDto(AssemblyMemberEraco e) {
        return AssemblyMemberSummaryDto.builder()
                .memberId(e.getMemberId().longValue())
                .name(e.getMember().getName() ) // 안전 처리
                .party(e.getParty().getPartyName())
                .age(e.getAge())
                .duty(e.getMember() != null ? e.getMember().getDuty() : null)
                .profileImage(e.getMember() != null ? e.getMember().getProfileImage() : null)
                .district(e.getElectionDistrict())
                .build();
    }

    public Page<AssemblyMemberSummaryDto> getAssemblyMemberAll(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return assemblyMemberRespotiroy.findAllAssemblyMember(pageable)
                .map(this::toSummaryDto);
    }
    public Page<AssemblyMemberSummaryDto> getAssemblyMemberEracoAll(
            int page, int size, String sortBy, String direction, String eraco
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                direction.equalsIgnoreCase("asc")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending()
        );

        Page<AssemblyMemberEraco> result;

        if (eraco != null && !eraco.isBlank()) {
            result = eracoRepository.findByEraco(eraco, pageable);
        } else {
            result = eracoRepository.findAll(pageable);
        }

        return result.map(this::toSummaryDto);
    }

    public AssemblyMemberDto getAssemblyMemberById(Long id) {

        AssemblyMember am = assemblyMemberRespotiroy.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No AssemblyMember found with id:" + id));

        List<String> eracoList = am.getEracos().stream()
                .map(AssemblyMemberEraco::getEraco)
                .toList();

        List<String> districtList = am.getEracos().stream()
                .map(AssemblyMemberEraco::getElectionDistrict)
                .toList();

        List<String> partyList = am.getEracos().stream()
                .map(e -> e.getParty() != null ? e.getParty().getPartyName() : null)
                .toList();

        List<CommitteeDto> committees = am.getCommittees().stream()
                .map(c->CommitteeDto.builder()
                        .id(c.getId())
                        .committeeCode(c.getCommitteeCode())
                        .committeeName(c.getCommitteeName())
                        .scheduleInfo(c.getScheduleInfo())
                        .activitiesDescription(c.getActivitiesDescription())
                        .build())
                .toList();

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
                .birthDate(am.getBirthDate().toString())
                .innerDuty(am.getInnerDuty())
                .build();



    }

    public Page<BillSummaryDto> getBillsByMemberId(Integer memberId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return billProposerRespotiroy.findBillsByMemberId(memberId, pageable);
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
                assemblyMemberRespotiroy.searchMembers(keyword, eraco, pageable);

        return resultPage.map(this::toDto);
    }
}
