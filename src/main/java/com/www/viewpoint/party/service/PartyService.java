package com.www.viewpoint.party.service;

import com.www.viewpoint.party.model.dto.*;
import com.www.viewpoint.party.model.entity.Party;
import com.www.viewpoint.party.respository.PartyRepository;
import com.www.viewpoint.share.dto.AssemblyMemberSummaryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PartyService {

    private final PartyRepository partyRepository;

    public PartyService(@Autowired PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    public Page<Party> getParties(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return partyRepository.findAll(pageable);
    }

    public PartySeatStatDto getPartySeatsByErcao(String eraco) {
        List<PartySeatCountProjection> projections= partyRepository.findPartySeatsByEraco(eraco);
        List<PartySeatCountDto> partySeatCounts = PartySeatCountDto.fromProjection(projections);

        // 전체 합계 계산
        long totalSeats = partySeatCounts.stream()
                .mapToLong(PartySeatCountDto::totalSeats)
                .sum();

        return new PartySeatStatDto(totalSeats, partySeatCounts);
    }

    public PartyMemberSummaryDto getPartyMemberSummary(String partyName, String eraco) {
        List<AssemblyMemberSummaryDto> members =
        partyRepository.findAllByPartyAndEracoIncludingSatellite(partyName, eraco)
                .stream()
                .map(it -> AssemblyMemberSummaryDto.builder()
                        .memberId(it.getMemberId().longValue())
                        .name(it.getName())
                        .party(it.getPartyName())   // 실제 컬럼명에 맞게 수정
                        .age(it.getAge())
                        .duty(it.getDuty())
                        .profileImage(it.getProfileImage())
                        .district(it.getDistrict()) // 실제 필드명에 맞춰 변경
                        .build()
                )
                .collect(Collectors.toList());

        if (members.isEmpty()) {
            return new PartyMemberSummaryDto(0, 0, 0, List.of());
        }

        long total = members.size();
        long proportional = members.stream()
                .filter(m -> "비례대표".equalsIgnoreCase(m.getDistrict()))
                .count();
        long district = total - proportional;

        return new PartyMemberSummaryDto(
                (int) total,
                (int) proportional,
                (int) district,
                members
        );
    }


    public Optional<Party> getPartyById(Integer id) {
        return partyRepository.findById(id);

    }
}

