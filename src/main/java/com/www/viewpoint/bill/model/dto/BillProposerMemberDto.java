package com.www.viewpoint.bill.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillProposerMemberDto {

    private Integer memberId;       // 발의 의원 id
    private String name;         // 의원 이름
    private String partyName;    // 정당명
    private Boolean isRepresentative; // 대표발의 여부
    private String eraco;        // 대수
}