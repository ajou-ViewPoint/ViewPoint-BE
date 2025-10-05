package com.www.viewpoint.bill.controller;

import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.bill.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bills API", description = "법안(Bill) 관련 API")
@RestController
@RequestMapping("/v1/bills")
public class BillController {

    private BillService billService;

    BillController(@Autowired BillService billService) {
        this.billService = billService;
    }


    @Operation(
            summary = "전체 법안 조회",
            description = "등록된 모든 법안을 페이지네이션과 정렬 옵션으로 조회합니다. 예시: /v1/bills?page=0&size=10&sortBy=billTitle&direction=asc"
    )
    @GetMapping
    public ResponseEntity<Page<Bill>> getBills(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 항목 수", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드 (예: id, billTitle, proposer, procResultCd 등)", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "정렬 방향 (asc: 오름차순, desc: 내림차순)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        Page<Bill> bills = billService.getBills(page, size, sortBy, direction);
        return ResponseEntity.ok(bills);
    }

    @Operation(
            summary = "ID로 법안 조회",
            description = "특정 ID의 법안을 반환합니다. 예시: /v1/bills/123"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(
            @Parameter(description = "조회할 법안의 ID", example = "123")
            @PathVariable Integer id) {
        return billService.getBillById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
