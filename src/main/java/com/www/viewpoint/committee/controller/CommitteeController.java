package com.www.viewpoint.committee.controller;

import com.www.viewpoint.committee.model.entity.Committee;
import com.www.viewpoint.committee.service.CommitteeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/committees")
@Tag(name = "위원회 API", description = "위원회 관련 API")
public class CommitteeController {

    private final CommitteeService committeeService;

    public CommitteeController(@Autowired CommitteeService committeeService) {
        this.committeeService = committeeService;
    }

    @Operation(
            summary = "전체 위원회 조회",
            description = "등록된 모든 위원회를 페이지네이션과 정렬 옵션으로 조회합니다. 예시: /v1/committees?page=0&size=10&sortBy=committeeName&direction=asc"
    )
    @GetMapping
    public ResponseEntity<Page<Committee>> getCommittees(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 항목 수", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드 (예: id, committeeName, committeeCode 등)", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "정렬 방향 (asc: 오름차순, desc: 내림차순)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        Page<Committee> committees = committeeService.getCommittees(page, size, sortBy, direction);
        return ResponseEntity.ok(committees);
    }

    @Operation(
            summary = "ID로 위원회 조회",
            description = "특정 ID의 위원회를 반환합니다. 예시: /v1/committees/3"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Committee> getCommitteeById(
            @Parameter(description = "조회할 위원회의 ID", example = "3")
            @PathVariable Integer id) {
        return committeeService.getCommitteeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "위원회 상세 (구성원 + 정당 통계)",
            description = """
                    committee_name을 기준으로 위원회 멤버 전체와 정당 분포 통계를 반환합니다.
                    예시: /v1/committees/detail?id=0
                    
                    응답:
                    {
                      "committeeName": "...",
                      "committeeId": 15,
                      "membersByRole": {
                        "위원": [
                          { "memberId": 123, "name": "...", "party": "...", ... },
                          ...
                        ],
                        "간사": [ ... ],
                        "위원장": [ ... ]
                      },
                      "stats": {
                        "더불어민주당": 15,
                        "국민의힘": 3,
                        "총 인원": 25
                      }
                    }
                    """
    )
    @GetMapping("/detail")
    public ResponseEntity<Map<String,Object>> getCommitteeDetail(
            @Parameter(description = "위원회 아이디로 조회", example = "3")
            @RequestParam("id") Integer id
    ) {
        Optional<Map<String,Object>> result = committeeService.getCommitteeMembersAndStats(id);
        return result
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

