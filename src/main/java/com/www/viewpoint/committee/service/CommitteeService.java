package com.www.viewpoint.committee.service;

import com.www.viewpoint.committee.dto.MemberWithRole;
import com.www.viewpoint.committee.dto.MemberWithRoleProjection;
import com.www.viewpoint.committee.model.entity.Committee;
import com.www.viewpoint.committee.repository.CommitteeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommitteeService {

    private final CommitteeRepository committeeRepository;

    public CommitteeService(@Autowired CommitteeRepository committeeRepository) {
        this.committeeRepository = committeeRepository;
    }

    public Page<Committee> getCommittees(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return committeeRepository.findAll(pageable);
    }

    public Optional<Committee> getCommitteeById(Integer id) {
        return committeeRepository.findById(id);
    }

    public Optional<Map<String,Object>> getCommitteeMembersAndStats(String committeeName) {
        Optional<Committee> opt = committeeRepository.findByCommitteeName(committeeName);
        if (opt.isEmpty()) {
            return Optional.empty();
        }

        Committee committee = opt.get();
        Integer committeeId = committee.getId();

        List<MemberWithRoleProjection> rawMembers = committeeRepository.findMembersByCommitteeId(committeeId);

        Map<String, List<MemberWithRole>> roleBuckets = new LinkedHashMap<>();

        for (MemberWithRoleProjection r : rawMembers) {
            String role = (r.getRole() == null || r.getRole().isBlank())
                    ? "위원"
                    : r.getRole();
            MemberWithRole m = MemberWithRole.builder()
                    .memberId(r.getMemberId())
                    .role(role)
                    .name(r.getName())
                    .age(r.getAge())
                    .duty(r.getDuty())
                    .party(r.getPartyName())
                    .district(r.getElectionDistrict())
                    .profileImage(r.getProfileImage())
                    .build();
            roleBuckets.computeIfAbsent(role, k -> new ArrayList<>())
                    .add(m);
        }

        List<Object[]> partyCounts = committeeRepository.countPartyDistribution(committeeId);

        Map<String,Object> stats = new LinkedHashMap<>();
        int total = 0;
        for (Object[] row : partyCounts) {
            String partyRaw = (String) row[0];
            if (partyRaw == null || partyRaw.isBlank()) continue;

            String[] parts = partyRaw.split("/");
            String party = parts[parts.length - 1].trim();

            Long cntLong;
            if (row[1] instanceof Long) {
                cntLong = (Long) row[1];
            } else if (row[1] instanceof Number) {
                cntLong = ((Number) row[1]).longValue();
            } else {
                cntLong = 0L;
            }

            stats.merge(party, cntLong, (a, b) -> ((Long) a) + ((Long) b));
            total += cntLong.intValue();
        }
        stats.put("총 인원", total);

        Map<String,Object> response = new LinkedHashMap<>();
        response.put("committeeName", committee.getCommitteeName());
        response.put("committeeId", committeeId);
        response.put("membersByRole", roleBuckets);
        response.put("stats", stats);

        return Optional.of(response);
    }
}
