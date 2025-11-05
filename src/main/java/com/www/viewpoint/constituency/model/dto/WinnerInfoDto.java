package com.www.viewpoint.constituency.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WinnerInfoDto {
    private String eraco;
    private String memberName;
    private String partyName;
    private String electionDistrict;
    private String sdName;
    private String wiwName;
    private BigDecimal voteRate;
    private String normalizedPartyName;
    private Integer memberId;          // ✅ 추가
    private String profileImage;    // ✅ 추가
}