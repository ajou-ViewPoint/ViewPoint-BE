package com.www.viewpoint.share.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AssemblyMemberSummaryDto {

    private Long id;
    private String name;
    private String party;
    private Integer age;
    private String duty;
    private String profileImage;
    private String district;

}