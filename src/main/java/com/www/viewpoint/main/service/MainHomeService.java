package com.www.viewpoint.main.service;

import com.www.viewpoint.assemblymember.model.dto.AssemblyMemberQueryProjection;
import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.bill.repository.BillRepository;
import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import com.www.viewpoint.assemblymember.repository.AssemblyMemberRepository;
import com.www.viewpoint.committee.model.entity.Committee;
import com.www.viewpoint.committee.repository.CommitteeRepository;
import com.www.viewpoint.main.dto.GlobalSearchResponse;
import com.www.viewpoint.main.dto.MemberSimpleDto;
import com.www.viewpoint.main.dto.MainHomeResponse;
import com.www.viewpoint.main.repository.MemberSimpleProjection;
import com.www.viewpoint.share.dto.AssemblyMemberSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MainHomeService {

    private final RecentBillService recentBillService;
    private final AssemblyMemberRepository assemblyMemberRepository;

    private final BillRepository billRepository;
    private final CommitteeRepository committeeRepository;

    public MainHomeService(RecentBillService recentBillService,
                           AssemblyMemberRepository assemblyMemberRepository,
                           BillRepository billRepository,
                           CommitteeRepository committeeRepository) {
        this.recentBillService = recentBillService;
        this.assemblyMemberRepository = assemblyMemberRepository;
        this.billRepository = billRepository;
        this.committeeRepository = committeeRepository;
    }

    private List<Bill> pickRecentBillsTop3() {
        return recentBillService.getTop3RecentBills();
    }

    private List<AssemblyMemberSummaryDto> pickRandomMembersForAge(Integer age) {
        List<AssemblyMemberQueryProjection> members =
                (age != null && age > 0)
                        ? assemblyMemberRepository.findRandomByAgeLimit8WithDistrict(age)
                        : assemblyMemberRepository.findRandomLimit8WithDistrict();

        if (members == null || members.isEmpty()) { // fallback
            members = assemblyMemberRepository.findRandomLimit8WithDistrict();
        }

        return members.stream()
                .map(m -> AssemblyMemberSummaryDto.builder()
                        .memberId(m.getMemberId())
                        .party(m.getParty())
                        .duty(m.getDuty())
                        .name(m.getName())
                        .age(m.getAge())
                        .profileImage(m.getProfileImage())
                        .district(m.getDistrict())   // 여기서 district 세팅
                        .build())
                .collect(Collectors.toList());
    }

    public MainHomeResponse getMainHomeData() {
        List<Bill> recentBills = pickRecentBillsTop3();
        Integer targetAge = recentBills.isEmpty() ? null : recentBills.get(0).getAge();

        List<AssemblyMemberSummaryDto> members = pickRandomMembersForAge(targetAge);

        return MainHomeResponse.builder()
                .recentBills(recentBills)
                .members(members)
                .build();
    }

    public GlobalSearchResponse globalSearch(String rawKeyword) {
        if (rawKeyword == null || rawKeyword.trim().isEmpty()) {
            return GlobalSearchResponse.builder()
                    .bills(List.of())
                    .members(List.of())
                    .committees(List.of())
                    .build();
        }

        String keyword = rawKeyword.trim();

        // 1) 법안 검색: 제목 / 요약 / 발의자 부분 일치, 최대 100건
        Page<Bill> billPage = billRepository
                .findByBillTitleContainingIgnoreCaseOrBillSummaryContainingIgnoreCaseOrProposerContainingIgnoreCase(
                        keyword, keyword, keyword,
                        PageRequest.of(0, 100)
                );
        List<Bill> bills = billPage.getContent();

        // 2) 국회의원 검색: 이름 / 정당 / 지역구 부분 일치
        //    Page 기반이라 일단 충분히 넉넉한 사이즈로 첫 페이지만 조회
        Page<AssemblyMemberQueryProjection> memberPage =
                assemblyMemberRepository.searchMembers(
                        keyword,
                        null, // eraco 필터 없음. 필요하면 파라미터 추가 가능
                        PageRequest.of(0, 1000)
                );
        List<AssemblyMemberQueryProjection> members = memberPage.getContent();

        // 3) 위원회 검색: 이름 부분 일치
        List<Committee> committees =
                committeeRepository.findByCommitteeNameContainingIgnoreCase(keyword);

        return GlobalSearchResponse.builder()
                .bills(bills)
                .members(members)
                .committees(committees)
                .build();
    }
}