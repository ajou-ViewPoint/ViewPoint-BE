package com.www.viewpoint.constituency.controller;

import com.www.viewpoint.constituency.model.dto.WinnerInfoDto;
import com.www.viewpoint.constituency.model.entity.Constituency;
import com.www.viewpoint.constituency.service.ConstituencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/constituencies")
@Tag(name = "선거구 API", description = "선거구 관련 API")
public class ConstituencyController {

    private final ConstituencyService constituencyService;

    public ConstituencyController(@Autowired ConstituencyService constituencyService) {
        this.constituencyService = constituencyService;
    }

    @Operation(
            summary = "전체 선거구 조회",
            description = "등록된 모든 선거구를 페이지네이션과 정렬 옵션으로 조회합니다. 예시: /v1/constituencies?page=0&size=10&sortBy=constName&direction=asc"
    )
    @GetMapping
    public ResponseEntity<Page<Constituency>> getConstituencies(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 항목 수", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드 (예: id, constName, constCode, estabDate 등)", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "정렬 방향 (asc: 오름차순, desc: 내림차순)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        Page<Constituency> constituencies = constituencyService.getConstituencies(page, size, sortBy, direction);
        return ResponseEntity.ok(constituencies);
    }

    @Operation(
            summary = "ID로 선거구 조회",
            description = "특정 ID의 선거구를 반환합니다. 예시: /v1/constituencies/7"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Constituency> getConstituencyById(
            @Parameter(description = "조회할 선거구의 ID", example = "7")
            @PathVariable Integer id) {
        return constituencyService.getConstituencyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "시도/시군구별 의원 조회")
    @GetMapping("/members")
    public ResponseEntity<List<WinnerInfoDto>> getMembersByConstituency(
            @Parameter(description = "시도명", example = "서울특별시")
            @RequestParam(required = false) String sido,
            @Parameter(description = "시군구명", example = "강남구")
            @RequestParam(required = false) String gungu,
            @Parameter(description = "국회 대수", example = "제22대")
            @RequestParam(required = false) String eracos
    ) {
        return ResponseEntity.ok(
                constituencyService.findMembersByRegion(sido, gungu, eracos)
        );
    }
}

