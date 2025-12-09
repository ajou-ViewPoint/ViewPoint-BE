package com.www.viewpoint.party.controller;

import com.www.viewpoint.config.DotenvInitializer;
import com.www.viewpoint.party.model.dto.PartyMemberSummaryDto;
import com.www.viewpoint.party.model.dto.PartySeatStatDto;
import com.www.viewpoint.party.model.entity.Party;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = DotenvInitializer.class)
class PartyControllerTest {

    @Autowired
    private PartyController partyController;

    @Test
    @DisplayName("전체 정당 조회 테스트")
    void testGetParties() {
        // given
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "desc";

        // when
        ResponseEntity<Page<Party>> response = partyController.getParties(page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("ID로 정당 조회 테스트")
    void testGetPartyById() {
        // given - 먼저 전체 조회해서 실제 존재하는 ID 가져오기
        ResponseEntity<Page<Party>> allParties = partyController.getParties(0, 1, "id", "asc");
        assertThat(allParties.getBody()).isNotNull();
        assertThat(allParties.getBody().getContent()).isNotEmpty();
        
        Integer id = allParties.getBody().getContent().get(0).getId();

        // when
        ResponseEntity<Party> response = partyController.getPartyById(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 정당 조회 시 404 반환")
    void testGetPartyByIdNotFound() {
        // given
        Integer nonExistentId = 999999;

        // when
        ResponseEntity<Party> response = partyController.getPartyById(nonExistentId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("정당별 의원 목록 조회 테스트")
    void testGetPartyMembersByParams() {
        // given
        String partyName = "더불어민주당";
        String eraco = "제22대";

        // when
        ResponseEntity<PartyMemberSummaryDto> response = partyController.getPartyMembersByParams(partyName, eraco);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().totalMembers()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("정당별 의석 통계 조회 테스트")
    void testGetPartySeatsByEraco() {
        // given
        String eraco = "제22대";

        // when
        ResponseEntity<PartySeatStatDto> response = partyController.getPartySeatsByEraco(eraco);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().totalSeats()).isGreaterThan(0);
        assertThat(response.getBody().partySeatStats()).isNotEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 eraco로 의석 통계 조회 시 404 반환")
    void testGetPartySeatsByNonExistentEraco() {
        // given
        String eraco = "제999대";

        // when
        ResponseEntity<PartySeatStatDto> response = partyController.getPartySeatsByEraco(eraco);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("페이지네이션 테스트")
    void testPagination() {
        // when
        ResponseEntity<Page<Party>> firstPage = partyController.getParties(0, 5, "id", "asc");
        ResponseEntity<Page<Party>> secondPage = partyController.getParties(1, 5, "id", "asc");

        // then
        assertThat(firstPage.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(secondPage.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        if (!firstPage.getBody().getContent().isEmpty() && !secondPage.getBody().getContent().isEmpty()) {
            assertThat(firstPage.getBody().getContent().get(0).getId())
                    .isNotEqualTo(secondPage.getBody().getContent().get(0).getId());
        }
    }

    @Test
    @DisplayName("정렬 테스트 - ID 내림차순")
    void testSortByIdDesc() {
        // when
        ResponseEntity<Page<Party>> response = partyController.getParties(0, 10, "id", "desc");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        var parties = response.getBody().getContent();
        if (parties.size() > 1) {
            for (int i = 0; i < parties.size() - 1; i++) {
                assertThat(parties.get(i).getId()).isGreaterThanOrEqualTo(parties.get(i + 1).getId());
            }
        }
    }
}

