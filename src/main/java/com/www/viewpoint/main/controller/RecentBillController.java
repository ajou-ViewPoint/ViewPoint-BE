package com.www.viewpoint.main.controller;

import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.main.service.RecentBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "Main API",
        description = "메인 화면용 API (최신 법안 등)"
)
@RestController
@RequestMapping("/v1/main")
public class RecentBillController {

    private final RecentBillService recentBillService;

    public RecentBillController(RecentBillService recentBillService) {
        this.recentBillService = recentBillService;
    }

    @Operation(
            summary = "메인용 최신 법안 3개",
            description = "propose_dt 기준으로 가장 최근 발의된 법안부터 최대 3개를 반환합니다. " +
                    "가장 최신 날짜의 법안이 3개 미만이면 다음 날짜 것도 포함해서 3개를 채웁니다."
    )
    @GetMapping("/recent-bills")
    public ResponseEntity<List<Bill>> getRecentBills() {
        List<Bill> recent = recentBillService.getTop3RecentBills();
        return ResponseEntity.ok(recent);
    }
}
