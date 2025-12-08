package com.www.viewpoint.constituency.controller;

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
class ConstituencyControllerTest {

    @Autowired
    private ConstituencyController constituencyController;

    @Test
    @DisplayName("랜덤 지역 코드 반환 테스트")
    void testGetRandomDistrictCode() {
        // when
        String code = constituencyController.getRandomDistrictCode();

        // then
        assertThat(code).isNotNull();
        assertThat(code).hasSize(5);
    }

    @Test
    @DisplayName("좌표 기반 지역 국회의원 조회 테스트")
    void testFindByCoords() {
        // given - 서울 좌표
        double lon = 127.023031;
        double lat = 37.245542;

        // when
        List<WinnerInfoProjection> result = constituencyController.findByCoords(lon, lat);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("지역 코드 기반 조회 테스트 - sido만")
    void testFindByRegionSidoOnly() {
        // given
        String sido = "서울특별시";

        // when
        List<WinnerInfoProjection> result = constituencyController.findByRegion(sido, null, null);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("지역 코드 기반 조회 테스트 - sido와 sgg")
    void testFindByRegionSidoAndSgg() {
        // given
        String sido = "서울특별시";
        String sgg = "강동구";

        // when
        List<WinnerInfoProjection> result = constituencyController.findByRegion(sido, sgg, null);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("지역 코드 기반 조회 테스트 - code만")
    void testFindByRegionCodeOnly() {
        // given - 랜덤 코드 먼저 가져오기
        String code = constituencyController.getRandomDistrictCode();

        // when
        List<WinnerInfoProjection> result = constituencyController.findByRegion(null, null, code);

        // then
        assertThat(result).isNotNull();
    }
}

