package com.www.viewpoint.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 의원 카드용
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSimpleDto {
    private String naasCode;
    private String name;
    private String profileImage;
}