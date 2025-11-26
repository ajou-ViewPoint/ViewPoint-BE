package com.www.viewpoint.bill.controller;

import com.www.viewpoint.bill.model.dto.BillProposerMemberDto;
import com.www.viewpoint.bill.model.dto.BillVoteSummaryDto;
import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.bill.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Tag(name = "Bills API", description = "법안(Bill) 관련 API")
@RestController
@RequestMapping("/v1/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    private static final Set<String> ALLOWED_PROC_RESULT_CODES = Set.of(
            "대안반영폐기",
            "부결",
            "불성립",
            "수정가결",
            "수정안반영폐기",
            "원안가결",
            "임기만료폐기",
            "철회"
    );

    @Operation(
            summary = "전체 법안 조회",
            description = "등록된 모든 법안을 페이지네이션과 정렬 옵션으로 조회합니다. 예시: /v1/bills?page=0&size=10&sortBy=billTitle&direction=asc"
    )
    @GetMapping
    public ResponseEntity<Page<Bill>> getBills(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 항목 수", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드 (예: id, billTitle, proposer, procResultCd 등)", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "정렬 방향 (asc: 오름차순, desc: 내림차순)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        Page<Bill> bills = billService.getBills(page, size, sortBy, direction);
        return ResponseEntity.ok(bills);
    }

    @Operation(
            summary = "ID로 법안 조회",
            description = "특정 ID의 법안을 반환합니다. 예시: /v1/bills/PRC_Z2Z3C0L1D1G3J1M0A4C8S0V0T8B5N2"
    )
    @GetMapping("/{billId}")
    public ResponseEntity<Bill> getBillById(
            @Parameter(description = "조회할 법안의 ID", example = "PRC_Z2Z3C0L1D1G3J1M0A4C8S0V0T8B5N2")
            @PathVariable String billId) {
        var bill = billService.getBillById(billId);
        if (bill == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(billService.getBillById(billId));

    }

    @Operation(
            summary = "키워드 기반 법안 검색",
            description = "bill_title / bill_summary / proposer 에 keyword가 포함된 법안 목록을 반환합니다. 예: /v1/bills/search?keyword=의료"
    )
    @GetMapping("/search")
    public ResponseEntity<List<Bill>> searchBillsByKeyword(
            @Parameter(description = "검색어 (부분 일치, 대소문자 무시)", example = "의료")
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        List<Bill> result = billService.searchBillsByKeyword(keyword);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "발의일 범위로 법안 검색",
            description = "propose_dt가 [start, end] 사이인 법안을 반환합니다. 날짜는 yyyy-MM-dd 형식. " +
                    "둘 중 하나만 줘도 동작합니다. 예: /v1/bills/search-by-date?start=2025-10-01&end=2025-10-30"
    )
    @GetMapping("/search-by-date")
    public ResponseEntity<List<Bill>> searchBillsByDateRange(
            @Parameter(description = "검색 시작일 (포함), 예: 2025-10-01", example = "2025-10-01")
            @RequestParam(name = "start", required = false) String startStr,
            @Parameter(description = "검색 종료일 (포함), 예: 2025-10-30", example = "2025-10-30")
            @RequestParam(name = "end", required = false) String endStr
    ) {
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (startStr != null && !startStr.isBlank()) {
            startDate = LocalDate.parse(startStr); // 형식 안 맞으면 400 나도 괜찮음 (Spring이 예외 던짐)
        }
        if (endStr != null && !endStr.isBlank()) {
            endDate = LocalDate.parse(endStr);
        }

        List<Bill> result = billService.searchBillsByDateRange(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "법안 제안자 조회",
            description = "특정 법안 ID로 제안자 목록을 반환합니다."
    )
    @GetMapping("/{billId}/proposers")
    public ResponseEntity<List<BillProposerMemberDto>> getProposersByBillId(
            @Parameter(description = "법안 ID", example = "PRC_Z2Z3C0L1D1G3J1M0A4C8S0V0T8B5N2")
            @PathVariable String  billId
    ) {
        List<BillProposerMemberDto> proposers = billService.getProposersByBillId(billId);
        if (proposers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(proposers);
    }

    @Operation(
            summary = "특정 법안의 투표 결과 요약 조회",
            description = """
                법안에 대해 찬성/반대/기권/불참한 국회의원 리스트를 반환합니다.
                
                예시 요청:
                GET /v1/bills/PRC_Z2Z3C0L1D1G3J1M0A4C8S0V0T8B5N2/votes
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "법안을 찾을 수 없음")
    })
    @GetMapping("/{billId}/votes")
    public ResponseEntity<BillVoteSummaryDto>  getBillVoteSummary(
            @PathVariable @Parameter(description = "법안의 billId (ARC_xxx 형식)",example ="PRC_Z2Z3C0L1D1G3J1M0A4C8S0V0T8B5N2") String billId
    ) {
        BillVoteSummaryDto summary = billService.getBillVoteResult(billId);

        // 데이터가 완전히 비었으면 404로 반환 (선택)
        if (summary.getAgree().isEmpty() &&
                summary.getDisagree().isEmpty() &&
                summary.getAbstain().isEmpty() &&
                summary.getAbsent().isEmpty()) {

            return ResponseEntity.notFound().build();
        }

       return ResponseEntity.ok(summary);
    }


    @Operation(
            summary = "복합 필터 기반 법안 검색",
            description = """
                아래 필터 중 하나 이상을 조합하여 법안을 검색합니다.
                
                - keyword: bill_title / bill_summary / proposer 에 부분 일치 검색
                - start, end: propose_dt 발의일 범위 (yyyy-MM-dd)
                - age: 발의 대수 (예: 21)
                - party: 발의 의원 정당명
                - procResultCd: 심사 단계 코드
                  (대안반영폐기, 부결, 불성립, 수정가결, 수정안반영폐기, 원안가결, 임기만료폐기, 철회 만 허용)
                
                예시:
                /v1/bills/filter?keyword=의료&start=2025-10-01&end=2025-10-31&age=22&party=더불어민주당&procResultCd=원안가결
                """
    )
    @GetMapping("/filter")
    public ResponseEntity<Page<Bill>> filterBills(
            @Parameter(description = "검색어 (bill_title / bill_summary / proposer 부분 일치)", example = "의료")
            @RequestParam(name = "keyword", required = false) String keyword,

            @Parameter(description = "발의 시작일 (포함, yyyy-MM-dd)", example = "2025-10-01")
            @RequestParam(name = "start", required = false) String startStr,

            @Parameter(description = "발의 종료일 (포함, yyyy-MM-dd)", example = "2025-10-31")
            @RequestParam(name = "end", required = false) String endStr,

            @Parameter(description = "발의 대수 (예: 21)", example = "22")
            @RequestParam(name = "age", required = false) Integer age,

            @Parameter(description = "발의 의원 정당명", example = "더불어민주당")
            @RequestParam(name = "party", required = false) String party,

            @Parameter(
                    description = """
                        심사 단계 코드 (proc_result_cd).
                        허용 값: 대안반영폐기, 부결, 불성립, 수정가결, 수정안반영폐기, 원안가결, 임기만료폐기, 철회
                        """,
                    example = "원안가결"
            )
            @RequestParam(name = "procResultCd", required = false) String procResultCd,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,

            @Parameter(description = "페이지당 항목 수", example = "20")
            @RequestParam(name = "size", defaultValue = "20") int size,

            @Parameter(description = "정렬 기준 필드 (예: billId, proposeDt 등)", example = "proposeDt")
            @RequestParam(name = "sortBy", defaultValue = "proposeDt") String sortBy,

            @Parameter(description = "정렬 방향 (asc / desc)", example = "desc")
            @RequestParam(name = "direction", defaultValue = "desc") String direction
    ) {
        // 1) procResultCd 유효성 검증
        if (procResultCd != null && !procResultCd.isBlank()) {
            String trimmed = procResultCd.trim();
            if (!ALLOWED_PROC_RESULT_CODES.contains(trimmed)) {
                return ResponseEntity.badRequest().build();
            }
        }

        // 2) 필터 하나도 없으면 400
        boolean noFilter =
                (keyword == null || keyword.isBlank()) &&
                        (startStr == null || startStr.isBlank()) &&
                        (endStr == null || endStr.isBlank()) &&
                        age == null &&
                        (party == null || party.isBlank()) &&
                        (procResultCd == null || procResultCd.isBlank());

        if (noFilter) {
            return ResponseEntity.badRequest().build();
        }

        // 3) 날짜 파싱
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (startStr != null && !startStr.isBlank()) {
            startDate = LocalDate.parse(startStr);
        }
        if (endStr != null && !endStr.isBlank()) {
            endDate = LocalDate.parse(endStr);
        }

        // 4) 정렬 + 페이지네이션 설정
        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // 메인 정렬 기준 + id 서브 정렬로 안정성 확보 (중복 페이지 방지)
        Sort sort = Sort.by(sortDirection, sortBy)
                .and(Sort.by(Sort.Direction.ASC, "id"));

        Pageable pageable = PageRequest.of(page, size, sort);

        // 5) 서비스 호출
        Page<Bill> result = billService.searchBillsWithFilters(
                keyword,
                startDate,
                endDate,
                age,
                party,
                procResultCd == null ? null : procResultCd.trim(),
                pageable
        );

        return ResponseEntity.ok(result);
    }
}
