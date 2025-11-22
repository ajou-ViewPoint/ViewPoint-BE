package com.www.viewpoint.bill.service;

import com.www.viewpoint.assemblymember.dto.AssemblyMemberSummaryDto;
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
                            v.getProfileImage()
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
}
