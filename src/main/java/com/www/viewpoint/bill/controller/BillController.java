package com.www.viewpoint.bill.controller;

import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.bill.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            description = "등록된 모든 법안을 반환합니다."
    )
    @GetMapping
    public ResponseEntity<Page<Bill>> getBills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Bill> bills = billService.getBills(page, size);
        return ResponseEntity.ok(bills);
    }

}
