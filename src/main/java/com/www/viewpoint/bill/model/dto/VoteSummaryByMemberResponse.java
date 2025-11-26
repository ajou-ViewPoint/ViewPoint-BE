package com.www.viewpoint.bill.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteSummaryByMemberResponse {
    private String billId;
    private String voteDate;
    private String billTitle;
    private String voteOpinion;
}