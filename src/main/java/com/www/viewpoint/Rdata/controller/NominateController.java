package com.www.viewpoint.Rdata.controller;

import com.www.viewpoint.Rdata.model.NominateResponseDto;
import com.www.viewpoint.Rdata.service.NominateService;
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

@Tag(name = "NOMINATE API", description = "Nominate 좌표 조회 API")
@RestController
@RequestMapping("/v1/nominate")
@RequiredArgsConstructor
public class NominateController {

    private final NominateService nominateService;

    @Operation(
            summary = "회기별 NOMINATE 좌표 조회",
            description = """
                    age(20,21,22)를 기준으로 의원별 NOMINATE 좌표를 반환합니다.
                    응답 형식 예:
                    [
                      {
                        "id": "이재명",
                        "data": [{ "x": -0.45, "y": -0.05, "party": "더불어민주당" }]
                      },
                      ...
                    ]
                    """
    )
    @GetMapping
    public ResponseEntity<List<NominateResponseDto>> getNominateByAge(
            @Parameter(description = "국회 회기 (예: 20, 21, 22)", example = "22")
            @RequestParam Integer age
    ) {
        List<NominateResponseDto> result = nominateService.getNominateByAge(age);
        return ResponseEntity.ok(result);
    }
}
