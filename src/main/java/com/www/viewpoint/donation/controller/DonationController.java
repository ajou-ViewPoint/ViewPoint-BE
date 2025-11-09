package com.www.viewpoint.donation.controller;

import com.www.viewpoint.donation.model.dto.DonationRequest;
import com.www.viewpoint.donation.model.entity.Donation;
import com.www.viewpoint.donation.service.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;
    @Operation(
    summary = "후원 생성 (Create Donation)",
    description = """
            ✅ **후원 생성 API**

            - 사용자가 입력한 이름, 이메일, 메시지, 금액을 받아 새로운 후원 데이터를 생성합니다.
            - 생성된 후원의 `donationId` 는 Toss 결제 과정에서 `orderId` 로 활용됩니다.

            ---
            ### 요청 예시
            ```json
            {
              "donorName": "홍길동",
              "donorEmail": "hong@test.com",
              "message": "좋은 일 응원합니다!",
              "amount": 10000
            }
            ```

            ### 응답 예시
            ```json
            {
              "donationId": "46b146da-d0b2-45c0-ab63-858924c42d85",
              "donorName": "홍길동",
              "donorEmail": "hong@test.com",
              "message": "좋은 일 응원합니다!",
              "amount": 10000,
              "createdAt": "2025-11-09T19:20:04.699559",
              "updatedAt": "2025-11-09T19:20:04.699559"
            }
            ```
            """
            )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "✅ 후원 생성 성공",
                    content = @Content(schema = @Schema(implementation = Donation.class))),
            @ApiResponse(responseCode = "400", description = "❌ 잘못된 입력값")
    })
    @PostMapping
    public ResponseEntity<Donation> createDonation(@RequestBody DonationRequest request) {
        Donation donation = donationService.createDonation(request);
        return ResponseEntity.ok(donation);
    }
    @Operation(
            summary = "전체 후원 내역 조회 (Get All Donations)",
            description = """
            ✅ **전체 후원 내역 조회 API**

            - 저장된 모든 후원 데이터를 반환합니다.  
            - 각 후원은 연결된 결제 정보(`TossPayment`)를 포함할 수 있습니다.
            - 프론트엔드에서 후원 내역 리스트를 표시할 때 사용됩니다.
            
            ---
            ### 응답 예시
            ```json
            [
              {
                "donationId": "46b146da-d0b2-45c0-ab63-858924c42d85",
                "donorName": "홍길동",
                "message": "좋은 일 응원합니다!",
                "amount": 10000,
                "createdAt": "2025-11-09T19:20:04.699559",
                "tossPayment": {
                  "paymentId": "tviva20251109192005Nj1j7",
                  "tossPaymentStatus": "DONE",
                  "tossPaymentMethod": "카드",
                  "totalAmount": 10000
                }
              }
            ]
            ```
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "✅ 후원 내역 조회 성공",
                    content = @Content(schema = @Schema(implementation = Donation.class)))
    })
    @GetMapping
    public ResponseEntity<List<Donation>> getAllDonations() {
        List<Donation> donations = donationService.findAllDonations();
        return ResponseEntity.ok(donations);
    }
}