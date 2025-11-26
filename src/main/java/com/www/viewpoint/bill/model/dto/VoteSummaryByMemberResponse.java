package com.www.viewpoint.bill.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteSummaryByMemberResponse {
    private Long billId;
    private String voteDate;
    private String title;
    private String voteOpinion;
}