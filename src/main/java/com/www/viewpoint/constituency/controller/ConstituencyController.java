package com.www.viewpoint.constituency.controller;

import com.www.viewpoint.constituency.model.dto.WinnerInfoDto;
import com.www.viewpoint.constituency.model.entity.Constituency;
import com.www.viewpoint.constituency.service.ConstituencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/constituencies")
@Tag(name = "선거구 API", description = "선거구 관련 API")
public class ConstituencyController {

    private final ConstituencyService constituencyService;

    public ConstituencyController(@Autowired ConstituencyService constituencyService) {
        this.constituencyService = constituencyService;
    }

//    @Operation(
//            summary = "전체 선거구 조회",
//            description = "등록된 모든 선거구를 페이지네이션과 정렬 옵션으로 조회합니다. 예시: /v1/constituencies?page=0&size=10&sortBy=constName&direction=asc"
//    )
//    @GetMapping
//    public ResponseEntity<Page<Constituency>> getConstituencies(
//            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
//            @RequestParam(defaultValue = "0") int page,
//            @Parameter(description = "페이지당 항목 수", example = "10")
//            @RequestParam(defaultValue = "10") int size,
//            @Parameter(description = "정렬 기준 필드 (예: id, constName, constCode, estabDate 등)", example = "id")
//            @RequestParam(defaultValue = "id") String sortBy,
//            @Parameter(description = "정렬 방향 (asc: 오름차순, desc: 내림차순)", example = "desc")
//            @RequestParam(defaultValue = "desc") String direction) {
//        Page<Constituency> constituencies = constituencyService.getConstituencies(page, size, sortBy, direction);
//        return ResponseEntity.ok(constituencies);
//    }
//
//    @Operation(
//            summary = "ID로 선거구 조회",
//            description = "특정 ID의 선거구를 반환합니다. 예시: /v1/constituencies/7"
//    )
//    @GetMapping("/{id}")
//    public ResponseEntity<Constituency> getConstituencyById(
//            @Parameter(description = "조회할 선거구의 ID", example = "7")
//            @PathVariable Integer id) {
//        return constituencyService.getConstituencyById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    @Operation(
            summary = "랜덤 지역 코드 반환",
            description = """
                korea_districts 테이블에서 code 길이 5짜리 지역을 랜덤으로 선택하여
                해당 code 하나만 반환합니다.
                """
    )
    @GetMapping("/random-district-code")
    public String getRandomDistrictCode() {
        return constituencyService.getRandomDistrictCode();
    }

    @Operation(
            summary = "좌표 기반 지역 국회의원 조회",
            description = """
                    위도(lat), 경도(lon)를 기반으로 사용자가 위치한 지역(행정구역)을 탐색하고,
                    해당 지역의 당선자 정보를 반환합니다.
                    <br>
                    예시 좌표: **lat=37.245542, lon=127.023031**
                    """
    )
    @GetMapping("/by-coords")
    public List<WinnerInfoDto> findByCoords(
            @Parameter(
                    description = "경도 (Longitude)",
                    example = "127.023031"
            )
            @RequestParam double lon,
            @Parameter(
                    description = "위도 (Latitude)",
                    example = "37.245542"
            )
            @RequestParam double lat
    ) {
        return constituencyService.findMembersByCoords(lon, lat );
    }
    @Operation(
            summary = "지역 코드 기반 조회",
            description = """
                    시도(sido), 시군구(sgg), 행정동 코드(code)(5자리 예시): 11110를 기준으로 
                    특정 지역의 당선자 정보를 조회합니다.
                    파라미터는 선택이며, 필요한 기준만 전달하면 됩니다.
                    map.geojson을 기준으로 합니다.
                    """
    )
    @GetMapping("/by-region")
    public List<WinnerInfoDto> findByRegion(
            @Parameter(description = "시도명 (예: 서울특별시)", example = "서울특별시")
            @RequestParam(required = false) String sido,
            @Parameter(description = "시군구명 (예: 강동구)", example = "강동구")
            @RequestParam(required = false) String sgg,
            @Parameter(description = "행정동 코드 5자리", example = "11740")
            @RequestParam(required = false) String code
    ) {
        return constituencyService.findMembersByRegion(sido, sgg, code);
    }
}

