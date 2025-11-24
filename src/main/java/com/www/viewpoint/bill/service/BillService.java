package com.www.viewpoint.bill.service;

import com.www.viewpoint.share.dto.AssemblyMemberSummaryDto;
import com.www.viewpoint.bill.model.dto.BillProposerMemberDto;
import com.www.viewpoint.bill.model.dto.BillVoteSummaryDto;
import com.www.viewpoint.bill.model.dto.VoteSummaryProjection;
import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.bill.model.entity.BillVoteResult;
import com.www.viewpoint.bill.repository.BillProposerRepository;
import com.www.viewpoint.bill.repository.BillRepository;
import com.www.viewpoint.bill.repository.BillVoteResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final BillProposerRepository billProposerRepository;
    private final BillVoteResultRepository billVoteResultRepository;

    public Page<Bill> getBills(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return billRepository.findAll(pageable);
    }

    public BillVoteSummaryDto getBillVoteResult(String billId) {

        List<VoteSummaryProjection> votes = billVoteResultRepository.findVoteSummary(billId);

        List<AssemblyMemberSummaryDto> agree = new ArrayList<>();
        List<AssemblyMemberSummaryDto> disagree = new ArrayList<>();
        List<AssemblyMemberSummaryDto> abstain = new ArrayList<>();
        List<AssemblyMemberSummaryDto> absent = new ArrayList<>();

        for (VoteSummaryProjection v : votes) {

            AssemblyMemberSummaryDto member =
                    new AssemblyMemberSummaryDto(
                            v.getId() ,
                            v.getName(),
                            v.getPartyName(),
                            v.getAge(),
                            v.getDuty(),
                            v.getProfileImage(),
                            v.getElectionDistrict()
                    );

            switch (v.getVoteOpinion()) {
                case "찬성" -> agree.add(member);
                case "반대" -> disagree.add(member);
                case "기권" -> abstain.add(member);
                case "불참" -> absent.add(member);
            }
        }

        return new BillVoteSummaryDto(agree, disagree, abstain, absent);
    }

    public List<BillProposerMemberDto> getProposersByBillId(String billId) {
        return billProposerRepository.findProposersByBillId(billId);
    }
    public Bill getBillById(String id) {
        return billRepository.findByBillId(id);
    }

    public List<Bill> searchBillsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return billRepository
                .findByBillTitleContainingIgnoreCaseOrBillSummaryContainingIgnoreCaseOrProposerContainingIgnoreCase(
                        keyword, keyword, keyword
                );
    }

    public List<Bill> searchBillsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return List.of();
        }
        if (startDate == null) {
            startDate = LocalDate.of(1900, 1, 1);
        }
        if (endDate == null) {
            endDate = LocalDate.of(2999, 12, 31);
        }
        if (startDate.isAfter(endDate)) {
            LocalDate tmp = startDate;
            startDate = endDate;
            endDate = tmp;
        }
        return billRepository.findByProposeDtBetween(startDate, endDate);
    }

    private Specification<Bill> andSpec(Specification<Bill> base, Specification<Bill> next) {
        return (base == null) ? next : base.and(next);
    }

    public List<Bill> searchBillsWithFilters(
            String keyword,
            LocalDate startDate,
            LocalDate endDate,
            Integer age,
            String party,
            String procResultCd
    ) {
        Specification<Bill> spec = null;

        // 1) 검색어 필터 (기존 /search 와 동일)
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase();

            Specification<Bill> keywordSpec = (root, query, cb) -> {
                var titleExpr   = cb.lower(root.get("billTitle"));
                var summaryExpr = cb.lower(root.get("billSummary"));
                var proposerExpr = cb.lower(root.get("proposer"));
                String pattern = "%" + kw + "%";

                return cb.or(
                        cb.like(titleExpr, pattern),
                        cb.like(summaryExpr, pattern),
                        cb.like(proposerExpr, pattern)
                );
            };

            spec = andSpec(spec, keywordSpec);
        }

        // 2) 발의 기간 필터 (proposeDt)
        if (startDate != null || endDate != null) {
            LocalDate s = (startDate == null) ? LocalDate.of(1900, 1, 1) : startDate;
            LocalDate e = (endDate == null) ? LocalDate.of(2999, 12, 31) : endDate;

            if (s.isAfter(e)) {
                LocalDate tmp = s;
                s = e;
                e = tmp;
            }

            LocalDate finalS = s;
            LocalDate finalE = e;

            Specification<Bill> dateSpec = (root, query, cb) ->
                    cb.between(root.get("proposeDt"), finalS, finalE);

            spec = andSpec(spec, dateSpec);
        }

        // 3) 발의 대수(age) 필터
        if (age != null) {
            Specification<Bill> ageSpec = (root, query, cb) ->
                    cb.equal(root.get("age"), age);

            spec = andSpec(spec, ageSpec);
        }

        // 4) 심사 단계(procResultCd) 필터
        if (procResultCd != null && !procResultCd.isBlank()) {
            String code = procResultCd.trim();

            Specification<Bill> procSpec = (root, query, cb) ->
                    cb.equal(root.get("procResultCd"), code);

            spec = andSpec(spec, procSpec);
        }

        // 5) 발의 의원 정당(party) 필터
        if (party != null && !party.isBlank()) {
            String partyName = party.trim();

            // 이 메서드는 이미 있다고 가정 (bill_id 리스트만 반환)
            List<String> billIds = billProposerRepository.findBillIdsByPartyName(partyName);

            if (billIds == null || billIds.isEmpty()) {
                // 해당 정당으로 발의한 법안이 하나도 없으면, 다른 필터와 관계 없이 결과는 빈 리스트
                return List.of();
            }

            Specification<Bill> partySpec = (root, query, cb) ->
                    root.get("billId").in(billIds);

            spec = andSpec(spec, partySpec);
        }

        // spec == null 이라는 것은 “아무 필터도 적용 안 됨” 이라는 뜻
        // - 컨트롤러에서 미리 체크해서 400을 던지거나
        // - 여기서 전체 조회를 허용하거나 둘 중 하나 선택
        if (spec == null) {
            // 1) 전체를 그냥 다 주고 싶으면
            // return billRepository.findAll();

            // 2) “적어도 한 개 필터는 있어야 한다” 정책이면
            return List.of(); // 또는 IllegalArgumentException 던지기
        }

        return billRepository.findAll(spec);
    }
}
