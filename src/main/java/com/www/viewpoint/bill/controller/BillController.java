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

import java.time.LocalDate;
import java.util.List;

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

    @Operation(
            summary = "키워드 기반 법안 검색",
            description = "bill_title / bill_summary / proposer 에 keyword가 포함된 법안 목록을 반환합니다. 예: /v1/bills/search?keyword=의료"
    )
    @GetMapping("/search")
    public ResponseEntity<List<Bill>> searchBillsByKeyword(
            @Parameter(description = "검색어 (부분 일치, 대소문자 무시)", example = "의료")
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        List<Bill> result = billService.searchBillsByKeyword(keyword);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "발의일 범위로 법안 검색",
            description = "propose_dt가 [start, end] 사이인 법안을 반환합니다. 날짜는 yyyy-MM-dd 형식. " +
                    "둘 중 하나만 줘도 동작합니다. 예: /v1/bills/search-by-date?start=2025-10-01&end=2025-10-30"
    )
    @GetMapping("/search-by-date")
    public ResponseEntity<List<Bill>> searchBillsByDateRange(
            @Parameter(description = "검색 시작일 (포함), 예: 2025-10-01", example = "2025-10-01")
            @RequestParam(name = "start", required = false) String startStr,
            @Parameter(description = "검색 종료일 (포함), 예: 2025-10-30", example = "2025-10-30")
            @RequestParam(name = "end", required = false) String endStr
    ) {
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (startStr != null && !startStr.isBlank()) {
            startDate = LocalDate.parse(startStr); // 형식 안 맞으면 400 나도 괜찮음 (Spring이 예외 던짐)
        }
        if (endStr != null && !endStr.isBlank()) {
            endDate = LocalDate.parse(endStr);
        }

        List<Bill> result = billService.searchBillsByDateRange(startDate, endDate);
        return ResponseEntity.ok(result);
    }
}
