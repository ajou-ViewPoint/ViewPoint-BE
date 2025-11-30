package com.www.viewpoint.Rdata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class NominateResponseDto {
    private String id;                    // 의원 이름 (예: "이재명")
    private List<NominatePointDto> data;  // [{x,y,party}]
}
