package com.www.viewpoint.share.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AssemblyMemberSummaryDto {

    private Long memberId;
    private String name;
    private String party;
    private Integer age;
    private String duty;
    private String profileImage;
    private String district;

}