package com.www.viewpoint.assemblymember.controller;

import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import com.www.viewpoint.assemblymember.service.AssemblyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            description = "등록된 모든 국회 의원 정보를 반환합니다."
    )
    @GetMapping
    public ResponseEntity<Page<AssemblyMember>> getAssemblyMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AssemblyMember> assemblyMembers = assemblyMemberService.getAssemblyMemberAll(page, size);
        return ResponseEntity.ok(assemblyMembers);
    }
}