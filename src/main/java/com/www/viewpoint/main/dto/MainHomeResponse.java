package com.www.viewpoint.main.dto;

import com.www.viewpoint.bill.model.entity.Bill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 홈 화면 API 전체 응답
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainHomeResponse {
    private List<Bill> recentBills;           // 최근 법안 3개
    private List<MemberSimpleDto> members;    // 랜덤 의원 8명 (간단 필드)
}