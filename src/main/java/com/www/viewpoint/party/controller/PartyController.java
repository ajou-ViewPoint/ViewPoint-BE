package com.www.viewpoint.party.controller;

import com.www.viewpoint.party.model.entity.Party;
import com.www.viewpoint.party.service.PartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/parties")
@Tag(name = "정당 API", description = "정당 관련 API")
public class PartyController {

    private final PartyService partyService;

    public PartyController(@Autowired PartyService partyService) {
        this.partyService = partyService;
    }

    @Operation(
            summary = "전체 정당 조회",
            description = "등록된 모든 정당을 페이지네이션과 정렬 옵션으로 조회합니다. 예시: /v1/parties?page=0&size=10&sortBy=partyName&direction=asc"
    )
    @GetMapping
    public ResponseEntity<Page<Party>> getParties(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 항목 수", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드 (예: id, partyName, foundedDate 등)", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "정렬 방향 (asc: 오름차순, desc: 내림차순)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        Page<Party> parties = partyService.getParties(page, size, sortBy, direction);
        return ResponseEntity.ok(parties);
    }

    @Operation(
            summary = "ID로 정당 조회",
            description = "특정 ID의 정당을 반환합니다. 예시: /v1/parties/5"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Party> getPartyById(
            @Parameter(description = "조회할 정당의 ID", example = "5")
            @PathVariable Integer id) {
        return partyService.getPartyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

