package com.www.viewpoint.Rdata.controller;

import com.www.viewpoint.Rdata.model.WordfishMemberTheta;
import com.www.viewpoint.Rdata.service.WordfishMemberThetaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Wordfish API", description = "위원회별 Wordfish(θ) 결과 조회 API")
@RestController
@RequestMapping("/v1/wordfish")
@RequiredArgsConstructor
public class WordfishMemberThetaController {

    private final WordfishMemberThetaService wordfishService;

    @Operation(
            summary = "위원회 Wordfish 결과 조회",
            description = """
                    committeeId 또는 committeeName 으로 위원회별 Wordfish 결과를 조회합니다.
                    - 예시: /v1/wordfish?committeeId=3
                    - 예시: /v1/wordfish?committeeName=교육위원회
                    - age(대수)를 함께 주면 해당 대수에 대해서만 필터링합니다.
                    """
    )
    @GetMapping
    public ResponseEntity<List<WordfishMemberTheta>> getWordfishByCommittee(
            @Parameter(description = "위원회 ID (Committee 테이블 PK)", example = "3")
            @RequestParam(required = false) Integer committeeId,

            @Parameter(description = "위원회 이름 (예: 교육위원회, 과학기술정보방송통신위원회)")
            @RequestParam(required = false) String committeeName,

            @Parameter(description = "대수 (예: 22). null 이면 모든 대수")
            @RequestParam(required = false) Integer age
    ) {
        if (committeeId == null && (committeeName == null || committeeName.isBlank())) {
            return ResponseEntity.badRequest().build();
        }

        List<WordfishMemberTheta> result;
        if (committeeId != null) {
            result = wordfishService.findByCommitteeId(committeeId, age);
        } else {
            result = wordfishService.findByCommitteeName(committeeName, age);
        }

        return ResponseEntity.ok(result);
    }
}
