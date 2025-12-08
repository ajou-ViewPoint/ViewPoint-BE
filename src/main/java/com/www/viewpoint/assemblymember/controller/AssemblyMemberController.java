package com.www.viewpoint.assemblymember.controller;

import com.www.viewpoint.assemblymember.model.dto.AssemblyMemberDto;
import com.www.viewpoint.assemblymember.service.AssemblyMemberService;
import com.www.viewpoint.bill.model.dto.BillSummaryDto;
import com.www.viewpoint.bill.model.dto.VoteSummaryByMemberResponse;
import com.www.viewpoint.bill.service.BillService;
import com.www.viewpoint.share.dto.AssemblyMemberSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AssemblyMember API", description = "국회의원(AssemblyMember) 관련 API")
@RestController
@AllArgsConstructor
@RequestMapping("/v1/assemblymembers")
public class AssemblyMemberController {

    private  final AssemblyMemberService assemblyMemberService;
    private final BillService billService;


    @Operation(
            summary = "전체 국회 의원 조회",
            description = "등록된 모든 국회 의원 정보를 페이지네이션과 정렬 옵션으로 조회합니다. 예시: /v1/assemblymembers?page=0&size=10&sortBy=name&direction=asc"
    )
    @GetMapping
    public ResponseEntity<Page<AssemblyMemberSummaryDto>> getAssemblyMembers(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 항목 수", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드 (예: id, name, party 등)", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "정렬 방향 (asc: 오름차순, desc: 내림차순)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        Page<AssemblyMemberSummaryDto> assemblyMembers = assemblyMemberService.getAssemblyMemberAll(page, size, sortBy, direction);
        return ResponseEntity.ok(assemblyMembers);
    }

    @Operation(
            summary = "ID로 국회의원 조회",
            description = "특정 ID의 국회의원을 반환합니다. 예시: /v1/assemblymembers/10"
    )
    @GetMapping("/{id}")
    public ResponseEntity<AssemblyMemberDto> getAssemblyMemberById(
            @Parameter(description = "조회할 국회의원의 ID", example = "5972")
            @PathVariable Long id) {
        return ResponseEntity.ok(assemblyMemberService.getAssemblyMemberById(id));

    }

    @Operation(
            summary = "대수별 국회의원 조회",
            description = "특정 대수(eraco)의 국회의원 목록을 페이지네이션과 함께 조회합니다. 예시: /v1/assemblymembers/eracos/제22대?page=0&size=10&sortBy=name&direction=asc"
    )
    @GetMapping("/eracos/{eraco}")
    public ResponseEntity<Page<AssemblyMemberSummaryDto>> getAssemblyMembersByEraco(
            @Parameter(description = "대수명 (예: 제22대)", example = "제22대")
            @PathVariable String eraco,

            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지당 데이터 수", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "정렬 기준", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "정렬 방향", example = "asc")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Page<AssemblyMemberSummaryDto> assemblyMembers =
                assemblyMemberService.getAssemblyMemberEracoAll( page, size, sortBy, direction,eraco);

        return ResponseEntity.ok(assemblyMembers);
    }


    @Operation(
            summary = "특정 의원이 발의한 법안 조회",
            description = "member_id 기준으로 의원이 발의한 모든 법안을 페이지네이션하여 조회합니다."
    )
    @GetMapping("/{id}/bills")
    public ResponseEntity<Page<BillSummaryDto>> getBillsByMemberId(
            @Parameter(description = "의원 ID", example = "1024")
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ) {
        Page<BillSummaryDto> bills = assemblyMemberService.getBillsByMemberId(id, page, size, "b.proposeDt", "desc");
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{memberId}/votes")
    public ResponseEntity<Page<VoteSummaryByMemberResponse>> getVoteSummary(
            @Parameter(description = "memberId", example = "5397")
            @PathVariable Long memberId,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지당 항목 수", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "voteDate"));

        Page<VoteSummaryByMemberResponse> response =
                billService.getVoteSummary(memberId, pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "복합 필터 기반 국회의원 검색",
            description = """
                아래 필터 중 하나 이상을 조합하여 국회의원을 검색합니다.
                
                - keyword: 의원 이름으로 부분 일치 검색
                - eraco: 재직 대수 (예: 제22대)
                - party: 정당명 (예: 더불어민주당)
                
                모든 필터는 선택사항이며, eraco entity에서 필터링한 후 memberId로 그룹화하여 반환합니다.
                
                예시:
                /v1/assemblymembers/filter?keyword=홍길동&eraco=제22대&party=더불어민주당&page=0&size=10&sortBy=name&direction=asc
                """
    )
    @GetMapping("/filter")
    public ResponseEntity<Page<AssemblyMemberDto>> filterAssemblyMembers(
            @Parameter(description = "의원 이름 검색어", example = "홍길동")
            @RequestParam(name = "keyword", required = false) String keyword,

            @Parameter(description = "재직 대수 (예: 제22대)", example = "제22대")
            @RequestParam(name = "eraco", required = false) String eraco,

            @Parameter(description = "정당명", example = "더불어민주당")
            @RequestParam(name = "party", required = false) String party,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,

            @Parameter(description = "페이지당 항목 수", example = "10")
            @RequestParam(name = "size", defaultValue = "10") int size,

            @Parameter(description = "정렬 기준 필드 (예: id, name 등)", example = "id")
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,

            @Parameter(description = "정렬 방향 (asc / desc)", example = "desc")
            @RequestParam(name = "direction", defaultValue = "desc") String direction
    ) {
        Page<AssemblyMemberDto> result = assemblyMemberService.filterAssemblyMembers(
                keyword, eraco, party, page, size, sortBy, direction
        );
        return ResponseEntity.ok(result);
    }

}