package com.www.viewpoint.party.service;

import com.www.viewpoint.party.model.dto.*;
import com.www.viewpoint.party.model.entity.Party;
import com.www.viewpoint.party.respository.PartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        List<PartyMemberInfoProjection> members =
                partyRepository.findAllByPartyAndEracoIncludingSatellite(partyName, eraco);

        if (members.isEmpty()) {
            return new PartyMemberSummaryDto(0, 0, 0, List.of());
        }

        long total = members.size();
        long proportional = members.stream()
                .filter(m -> "비례대표".equalsIgnoreCase(m.getConstituencyType()))
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

