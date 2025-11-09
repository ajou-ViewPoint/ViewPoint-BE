package com.www.viewpoint.bill.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillSummaryDto {
    private Integer billDbId;      // bill.id
    private String billId;         // bill.bill_id
    private String billTitle;      // 법안 제목
    private Boolean isRepresentative; // 대표발의 여부
    private String eraco;          // 대수
}