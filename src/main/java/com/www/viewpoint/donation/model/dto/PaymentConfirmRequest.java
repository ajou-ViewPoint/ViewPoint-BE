package com.www.viewpoint.donation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Toss 결제 승인 요청 DTO
 * successUrl redirect 이후 프론트엔드에서 서버로 보내는 데이터
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequest {

    /** 토스 결제 고유 키 (paymentKey) */
    private String paymentKey;

    /** 주문 번호 (Donation ID로 사용됨) */
    private String orderId;

    /** 결제 금액 */
    private Long amount;
}