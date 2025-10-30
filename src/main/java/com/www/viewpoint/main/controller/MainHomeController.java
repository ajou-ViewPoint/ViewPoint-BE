package com.www.viewpoint.main.controller;

import com.www.viewpoint.main.dto.MainHomeResponse;
import com.www.viewpoint.main.service.MainHomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Main API",
        description = "메인 화면용 API (최신 법안 + 랜덤 의원)"
)
@RestController
@RequestMapping("/v1/main")
public class MainHomeController {

    private final MainHomeService mainHomeService;

    public MainHomeController(MainHomeService mainHomeService) {
        this.mainHomeService = mainHomeService;
    }

    @Operation(
            summary = "메인 화면 데이터",
            description = "최근 발의된 법안 3개와 현 임기 현직 국회의원 중 랜덤 8명을 반환합니다."
    )
    @GetMapping("/home")
    public ResponseEntity<MainHomeResponse> getHomeData() {
        MainHomeResponse resp = mainHomeService.getMainHomeData();
        return ResponseEntity.ok(resp);
    }
}