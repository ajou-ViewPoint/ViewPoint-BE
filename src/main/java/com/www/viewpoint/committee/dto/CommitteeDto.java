package com.www.viewpoint.committee.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommitteeDto {
    private Integer id;
    private String committeeCode;
    private String activitiesDescription;
    private String scheduleInfo;
    private String committeeName;
}