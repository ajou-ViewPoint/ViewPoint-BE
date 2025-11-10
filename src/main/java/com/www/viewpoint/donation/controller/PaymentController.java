package com.www.viewpoint.donation.controller;
import com.www.viewpoint.donation.config.TossPaymentConfig;
import com.www.viewpoint.donation.model.dto.PaymentConfirmRequest;
import com.www.viewpoint.donation.model.entity.TossPayment;
import com.www.viewpoint.donation.model.enums.TossPaymentMethod;
import com.www.viewpoint.donation.model.enums.TossPaymentStatus;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.www.viewpoint.donation.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final TossPaymentConfig tossPaymentConfig;
    private final DonationService donationService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JSONObject parseRequestData(String jsonBody) {
        try {
            return (JSONObject) new JSONParser().parse(jsonBody);
        } catch (ParseException e) {
            logger.error("JSON Parsing Error", e);
            return new JSONObject();
        }
    }
    private HttpURLConnection createConnection(String secretKey, String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }

    private JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException {
        HttpURLConnection connection = createConnection(secretKey, urlString);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
             Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (Exception e) {
            logger.error("Error reading response", e);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error reading response");
            return errorResponse;
        }
    }

    @Operation(
            summary = "토스 결제 승인 (Payment Confirm)",
            description = """
        ✅ **결제 승인 API**
        
        - 클라이언트가 Toss 결제 성공 시 받은 `paymentKey`, `orderId`, `amount` 값을 서버에 전달합니다.  
        - 서버는 이 데이터를 바탕으로 Toss Payments 결제 승인 API를 호출하여 결제를 최종 확정합니다.  
        - 결제 완료 후 Donation(후원) 엔티티에 결제 정보를 연결합니다.
        
        ---
        ### 요청 흐름:
        - 후원하기 -> 후원 저장
        - 프론트엔드 → Toss SDK로 결제 요청  
        - 결제 성공 시 Toss SDK가 paymentKey, orderId, amount를 반환  
        - 프론트엔드 → 이 API(`/v1/payments/confirm`)에 JSON 전송  
        - 서버 → Toss Payments API로 승인 요청  
        - 성공 시 DB(Donation, TossPayment)에 결제정보 저장  
        - 결제 승인 결과를 JSON으로 반환
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ 결제 승인 성공",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "❌ 결제 승인 실패 (Toss API 오류 또는 잘못된 요청)",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmSuccessPayment(
            @RequestBody PaymentConfirmRequest request
            ) throws Exception {

        // 1️⃣ 결제 금액 검증 (DB 금액과 비교)
        var donation = donationService.findById(UUID.fromString(request.getOrderId()));

        if(donation == null) {
            return ResponseEntity.badRequest().body("일치하는 후원아이디가 없습니다");
        }

        if (!donation.getAmount().equals(request.getAmount())) {
            logger.error(donation.getAmount() + " : " + request.getAmount());
            return ResponseEntity.badRequest().body("결제 금액 불일치");
        }

        // 2️⃣ Toss 결제 승인 요청 (POST /v1/payments/confirm)
        JSONObject requestData = new JSONObject();
        requestData.put("paymentKey", request.getPaymentKey());
        requestData.put("orderId", request.getOrderId());
        requestData.put("amount", request.getAmount());

        JSONObject response = sendRequest(requestData,
                tossPaymentConfig.getSecretKey(),
                tossPaymentConfig.getPaymentConfirmUrl());

        String status = (String) response.get("status");
        String method = (String) response.get("method");
        String requestedAtStr = (String) response.get("requestedAt");
        String approvedAtStr = (String) response.get("approvedAt");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        LocalDateTime requestedAt = null;
        LocalDateTime approvedAt = null;
        try {
            if (requestedAtStr != null) {
                requestedAt = LocalDateTime.parse(requestedAtStr, formatter);
            }
            if (approvedAtStr != null) {
                approvedAt = LocalDateTime.parse(approvedAtStr, formatter);
            }
        } catch (Exception e) {
            logger.warn("날짜 파싱 실패: {}", e.getMessage());
        }

        logger.info("method: " + response.toString());
        TossPayment tossPayment =  TossPayment.builder()
                .paymentId(request.getPaymentKey())
                .tossPaymentStatus(TossPaymentStatus.valueOf(status))
                .tossPaymentMethod(TossPaymentMethod.valueOf(method))
                .donation(donation)
                .totalAmount(request.getAmount())
                .requestedAt(requestedAt)
                .approvedAt(approvedAt)
                .build();

        // 3️⃣ 성공 시 Donation에 결제정보 저장
        if (!response.containsKey("error")) {
            donationService.attachPaymentToDonation(
                    UUID.fromString(request.getOrderId()), tossPayment
            );
        }

        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "후원 취소 (Donation Cancel)",
            description = """
        ✅ **후원 취소 API**
        
        - 결제 되는 순간 fail일 경우 이 end point를 호출 합니다.
        - `Donation`과 연결된 `TossPayment` 또한 `CascadeType.ALL` 설정 시 자동으로 함께 제거됩니다.  
        - 주로 테스트 환경 또는 결제 실패/취소 시 롤백용으로 사용합니다.
        
        ---
        ### 요청 흐름:
        - 토스 ->fail 주소로 redirection 된 경우 
        - 프론트엔드에서 `orderId` (Donation ID) 전달  
        - 서버에서 해당 후원 데이터를 조회 후 삭제  
        - 삭제 성공 시 `200 OK` 반환
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ 후원 삭제(취소) 성공",
                    content = @Content(schema = @Schema(implementation = Void.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "❌ 해당 orderId에 대한 후원이 존재하지 않음",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping("/cancel")
    public ResponseEntity<?> confirmSuccessPayment(
            @RequestParam String orderId
    ){
        donationService.deleteById(UUID.fromString(orderId));
        return ResponseEntity.ok().build();
    }





}
