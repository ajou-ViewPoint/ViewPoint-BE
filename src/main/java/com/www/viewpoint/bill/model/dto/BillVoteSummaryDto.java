package com.www.viewpoint.bill.model.dto;


import com.www.viewpoint.assemblymember.dto.AssemblyMemberSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BillVoteSummaryDto {

    // 통계
    private int agreeCount;
    private int disagreeCount;
    private int abstainCount;
    private int absentCount;
    private int totalCount;

    private List<AssemblyMemberSummaryDto> agree;      // 찬성
    private List<AssemblyMemberSummaryDto> disagree;   // 반대
    private List<AssemblyMemberSummaryDto> abstain;    // 기권
    private List<AssemblyMemberSummaryDto> absent;     // 불참

    // 선택: 전체 인원
    public BillVoteSummaryDto(
            List<AssemblyMemberSummaryDto> agree,
            List<AssemblyMemberSummaryDto> disagree,
            List<AssemblyMemberSummaryDto> abstain,
            List<AssemblyMemberSummaryDto> absent
    ) {
        this.agree = agree;
        this.disagree = disagree;
        this.abstain = abstain;
        this.absent = absent;

        this.agreeCount = agree.size();
        this.disagreeCount = disagree.size();
        this.abstainCount = abstain.size();
        this.absentCount = absent.size();
        this.totalCount = agree.size() + disagree.size() + abstain.size() + absent.size();
    }
}