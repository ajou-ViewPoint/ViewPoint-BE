package com.www.viewpoint.party.controller;

import com.www.viewpoint.party.model.dto.PartyMemberInfoProjection;
import com.www.viewpoint.party.model.dto.PartyMemberSummaryDto;
import com.www.viewpoint.party.model.dto.PartySeatStatDto;
import com.www.viewpoint.party.model.entity.Party;
import com.www.viewpoint.party.service.PartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @Operation(
            summary = "정당별 의원 목록 조회 (위성정당 및 대수 포함)",
            description = "특정 정당명(partyName)과 대수(eraco)를 파라미터로 받아 해당 의원 목록을 조회합니다. "
                    + "예: /v1/parties/members?partyName=더불어민주당&eraco=제22대"
    )
    @GetMapping("/members")
    public ResponseEntity<PartyMemberSummaryDto> getPartyMembersByParams(
            @Parameter(description = "조회할 정당명", example = "더불어민주당")
            @RequestParam String partyName,

            @Parameter(description = "조회할 국회 대수", example = "제22대")
            @RequestParam String eraco
    ) {
        PartyMemberSummaryDto members =
                partyService.getPartyMemberSummary(partyName, eraco);

        return ResponseEntity.ok(members);
    }
    @Operation(
            summary = "정당별 의석 통계 조회",
            description = """
            특정 국회 대수(eraco)에 해당하는 정당별 의석 현황을 조회합니다.
            각 정당의 지역구 의석수, 비례대표 의석수, 그리고 총 의석수를 함께 반환합니다.
            예: /v1/parties/seats?eraco=제22대
            """
    )
    @GetMapping("/seats")
    public ResponseEntity<PartySeatStatDto> getPartySeatsByEraco(
            @Parameter(description = "조회할 국회 대수", example = "제22대")
            @RequestParam String eraco
    ) {
        PartySeatStatDto result = partyService.getPartySeatsByErcao(eraco);
        System.out.printf(result.toString());
        if (result == null || result.partySeatStats().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }
}

