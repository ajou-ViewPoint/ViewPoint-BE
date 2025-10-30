package com.www.viewpoint.main.service;

import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.bill.repository.BillRepository;
import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import com.www.viewpoint.assemblymember.repository.AssemblyMemberRepository;
import com.www.viewpoint.main.dto.MemberSimpleDto;
import com.www.viewpoint.main.dto.MainHomeResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MainHomeService {

    private final BillRepository billRepository;
    private final AssemblyMemberRepository assemblyMemberRepository;

    public MainHomeService(BillRepository billRepository,
                           AssemblyMemberRepository assemblyMemberRepository) {
        this.billRepository = billRepository;
        this.assemblyMemberRepository = assemblyMemberRepository;
    }

    // 1) 최근 법안 3개 로직 (이전 RecentBillService.getTop3RecentBills)
    private List<Bill> pickRecentBillsTop3() {
        List<Bill> allSorted = billRepository.findByProposeDtIsNotNullOrderByProposeDtDescIdDesc();
        if (allSorted.isEmpty()) {
            return List.of();
        }

        List<Bill> picked = new ArrayList<>(3);
        for (Bill bill : allSorted) {
            if (bill.getProposeDt() == null) continue;
            picked.add(bill);
            if (picked.size() >= 3) break;
        }
        return picked;
    }

    // 2) 의원 랜덤 8명 뽑기
    private List<MemberSimpleDto> pickRandomMembersForAge(Integer age) {
        List<AssemblyMember> members;

        if (age != null) {
            members = assemblyMemberRepository.findRandomByAgeLimit8(age);
            if (members == null || members.isEmpty()) {
                // fallback 전체 랜덤
                members = assemblyMemberRepository.findRandomLimit8();
            }
        } else {
            members = assemblyMemberRepository.findRandomLimit8();
        }

        List<MemberSimpleDto> dtoList = new ArrayList<>();
        for (AssemblyMember m : members) {
            dtoList.add(MemberSimpleDto.builder()
                    .naasCode(m.getNaasCode())
                    .name(m.getName())
                    .profileImage(m.getProfileImage())
                    .build()
            );
        }

        return dtoList;
    }

    // 3) 메인 홈 응답 조립
    public MainHomeResponse getMainHomeData() {
        // 최근 법안 3개
        List<Bill> recentBills = pickRecentBillsTop3();

        // 그중 가장 첫 번째(가장 최신) bill의 age를 기준으로 의원 랜덤 뽑자
        Integer targetAge = null;
        if (!recentBills.isEmpty()) {
            targetAge = recentBills.get(0).getAge(); // Bill 엔티티에 age 필드 있다고 가정
        }

        List<MemberSimpleDto> members = pickRandomMembersForAge(targetAge);

        return MainHomeResponse.builder()
                .recentBills(recentBills)
                .members(members)
                .build();
    }
}