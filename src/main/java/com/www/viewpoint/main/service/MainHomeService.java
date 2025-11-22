package com.www.viewpoint.main.service;

import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.bill.repository.BillRepository;
import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import com.www.viewpoint.assemblymember.repository.AssemblyMemberRepository;
import com.www.viewpoint.main.dto.MemberSimpleDto;
import com.www.viewpoint.main.dto.MainHomeResponse;
import com.www.viewpoint.main.repository.MemberSimpleProjection;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MainHomeService {

    private final RecentBillService recentBillService;
    private final AssemblyMemberRepository assemblyMemberRepository;

    public MainHomeService(RecentBillService recentBillService,
                           AssemblyMemberRepository assemblyMemberRepository) {
        this.recentBillService = recentBillService;
        this.assemblyMemberRepository = assemblyMemberRepository;
    }

    private List<Bill> pickRecentBillsTop3() {
        return recentBillService.getTop3RecentBills();
    }

    private List<MemberSimpleDto> pickRandomMembersForAge(Integer age) {
        List<MemberSimpleProjection> members =
                (age != null && age > 0)
                        ? assemblyMemberRepository.findRandomByAgeLimit8WithDistrict(age)
                        : assemblyMemberRepository.findRandomLimit8WithDistrict();

        if (members == null || members.isEmpty()) { // fallback
            members = assemblyMemberRepository.findRandomLimit8WithDistrict();
        }

        return members.stream()
                .map(m -> MemberSimpleDto.builder()
                        .naasCode(m.getNaasCode())
                        .name(m.getName())
                        .profileImage(m.getProfileImage())
                        .district(m.getDistrict())   // 여기서 district 세팅
                        .build())
                .toList();
    }

    public MainHomeResponse getMainHomeData() {
        List<Bill> recentBills = pickRecentBillsTop3();
        Integer targetAge = recentBills.isEmpty() ? null : recentBills.get(0).getAge();

        List<MemberSimpleDto> members = pickRandomMembersForAge(targetAge);

        return MainHomeResponse.builder()
                .recentBills(recentBills)
                .members(members)
                .build();
    }
}