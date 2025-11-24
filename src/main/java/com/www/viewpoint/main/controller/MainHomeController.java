package com.www.viewpoint.main.controller;

import com.www.viewpoint.main.dto.GlobalSearchResponse;
import com.www.viewpoint.main.dto.MainHomeResponse;
import com.www.viewpoint.main.service.MainHomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(
            summary = "전역 통합 검색",
            description = """
                    단일 검색어로 법안, 국회의원, 위원회를 한 번에 검색합니다.

                    1) 법안(bills)
                    - 검색 대상 컬럼: bill_title, bill_summary, proposer
                    - 부분 일치(contains, 대소문자 무시) 조건으로 검색합니다.
                    - 검색 결과가 매우 많을 수 있으므로 최대 100건까지만 반환합니다.

                    2) 국회의원(members)
                    - 검색 대상 컬럼: 이름(national_assembly_member.name), 정당명(party_name), 지역구명(election_district)
                    - AssemblyMemberRepository.searchMembers() 쿼리를 재사용합니다.
                    - 최신 재직 회기 정보(assembly_member_eraco + party)를 조인하여 함께 반환합니다.

                    3) 위원회(committees)
                    - 검색 대상 컬럼: committee_name
                    - 부분 일치(contains, 대소문자 무시) 조건으로 검색합니다.

                    검색어가 비어 있거나 공백만 있는 경우에는 모든 결과 리스트가 빈 배열로 반환됩니다.
                    """
    )
    @GetMapping("/search")
    public ResponseEntity<GlobalSearchResponse> globalSearch(
            @Parameter(
                    description = "통합 검색에 사용할 키워드. 법안 제목, 요약, 발의자, 의원 이름, 정당, 지역구, 위원회명에 대해 부분 일치 검색을 수행합니다.",
                    example = "경제"
            )
            @RequestParam("keyword") String keyword
    ) {
        GlobalSearchResponse resp = mainHomeService.globalSearch(keyword);
        return ResponseEntity.ok(resp);
    }
}