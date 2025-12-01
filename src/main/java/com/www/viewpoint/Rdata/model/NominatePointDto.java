package com.www.viewpoint.Rdata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NominatePointDto {
    private double x;
    private double y;
    private String party;
}