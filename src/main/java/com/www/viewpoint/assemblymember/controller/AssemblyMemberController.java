package com.www.viewpoint.assemblymember.controller;

import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import com.www.viewpoint.assemblymember.service.AssemblyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AssemblyMember API", description = "국회의원(AssemblyMember) 관련 API")
@RestController
@RequestMapping("/v1/assemblymembers")
public class AssemblyMemberController {

    private AssemblyMemberService assemblyMemberService;

    AssemblyMemberController(@Autowired AssemblyMemberService assemblyMemberService) {
        this.assemblyMemberService = assemblyMemberService;
    }

    @Operation(
            summary = "전체 국회 의원 조회",
            description = "등록된 모든 국회 의원 정보를 페이지네이션과 정렬 옵션으로 조회합니다. 예시: /v1/assemblymembers?page=0&size=10&sortBy=name&direction=asc"
    )
    @GetMapping
    public ResponseEntity<Page<AssemblyMember>> getAssemblyMembers(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 항목 수", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드 (예: id, name, party 등)", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "정렬 방향 (asc: 오름차순, desc: 내림차순)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        Page<AssemblyMember> assemblyMembers = assemblyMemberService.getAssemblyMemberAll(page, size, sortBy, direction);
        return ResponseEntity.ok(assemblyMembers);
    }

    @Operation(
            summary = "ID로 국회의원 조회",
            description = "특정 ID의 국회의원을 반환합니다. 예시: /v1/assemblymembers/10"
    )
    @GetMapping("/{id}")
    public ResponseEntity<AssemblyMember> getAssemblyMemberById(
            @Parameter(description = "조회할 국회의원의 ID", example = "10")
            @PathVariable Long id) {
        return assemblyMemberService.getAssemblyMemberById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}