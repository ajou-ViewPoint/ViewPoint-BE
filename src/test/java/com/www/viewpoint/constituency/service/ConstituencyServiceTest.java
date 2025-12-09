package com.www.viewpoint.constituency.service;

import com.www.viewpoint.config.DotenvInitializer;
import com.www.viewpoint.constituency.model.dto.WinnerInfoProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = DotenvInitializer.class)
class ConstituencyServiceTest {

    @Autowired
    private ConstituencyService constituencyService;

    @Test
    @DisplayName("랜덤 지역 코드 반환 테스트")
    void testGetRandomDistrictCode() {
        // when
        String code = constituencyService.getRandomDistrictCode();

        // then
        assertThat(code).isNotNull();
        assertThat(code).hasSize(5);
    }

    @Test
    @DisplayName("좌표 기반 의원 검색 테스트")
    void testFindMembersByCoords() {
        // given - 서울 좌표
        double lon = 127.023031;
        double lat = 37.245542;

        // when
        List<WinnerInfoProjection> result = constituencyService.findMembersByCoords(lon, lat);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("지역 기반 의원 검색 테스트 - sido만")
    void testFindMembersByRegionSidoOnly() {
        // given
        String sido = "서울특별시";

        // when
        List<WinnerInfoProjection> result = constituencyService.findMembersByRegion(sido, null, null);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("지역 기반 의원 검색 테스트 - sido와 sgg")
    void testFindMembersByRegionSidoAndSgg() {
        // given
        String sido = "서울특별시";
        String sgg = "강동구";

        // when
        List<WinnerInfoProjection> result = constituencyService.findMembersByRegion(sido, sgg, null);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("지역 기반 의원 검색 테스트 - code만")
    void testFindMembersByRegionCodeOnly() {
        // given - 랜덤 코드 먼저 가져오기
        String code = constituencyService.getRandomDistrictCode();

        // when
        List<WinnerInfoProjection> result = constituencyService.findMembersByRegion(null, null, code);

        // then
        assertThat(result).isNotNull();
    }
}

