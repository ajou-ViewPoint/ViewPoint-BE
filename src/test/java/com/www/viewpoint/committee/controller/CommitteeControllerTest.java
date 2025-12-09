package com.www.viewpoint.committee.controller;

import com.www.viewpoint.committee.model.entity.Committee;
import com.www.viewpoint.config.DotenvInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = DotenvInitializer.class)
class CommitteeControllerTest {

    @Autowired
    private CommitteeController committeeController;

    @Test
    @DisplayName("전체 위원회 조회 테스트")
    void testGetCommittees() {
        // given
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "desc";

        // when
        ResponseEntity<Page<Committee>> response = committeeController.getCommittees(page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("ID로 위원회 조회 테스트")
    void testGetCommitteeById() {
        // given - 먼저 전체 조회해서 실제 존재하는 ID 가져오기
        ResponseEntity<Page<Committee>> allCommittees = committeeController.getCommittees(0, 1, "id", "asc");
        assertThat(allCommittees.getBody()).isNotNull();
        assertThat(allCommittees.getBody().getContent()).isNotEmpty();
        
        Integer id = allCommittees.getBody().getContent().get(0).getId();

        // when
        ResponseEntity<Committee> response = committeeController.getCommitteeById(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 위원회 조회 시 404 반환")
    void testGetCommitteeByIdNotFound() {
        // given
        Integer nonExistentId = 999999;

        // when
        ResponseEntity<Committee> response = committeeController.getCommitteeById(nonExistentId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("위원회 상세 조회 테스트")
    void testGetCommitteeDetail() {
        // given - 먼저 전체 조회해서 실제 존재하는 ID 가져오기
        ResponseEntity<Page<Committee>> allCommittees = committeeController.getCommittees(0, 1, "id", "asc");
        assertThat(allCommittees.getBody()).isNotNull();
        assertThat(allCommittees.getBody().getContent()).isNotEmpty();
        
        Integer id = allCommittees.getBody().getContent().get(0).getId();

        // when
        ResponseEntity<Map<String, Object>> response = committeeController.getCommitteeDetail(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("committeeName");
        assertThat(response.getBody()).containsKey("committeeId");
        assertThat(response.getBody()).containsKey("membersByRole");
        assertThat(response.getBody()).containsKey("stats");
    }

    @Test
    @DisplayName("페이지네이션 테스트")
    void testPagination() {
        // when
        ResponseEntity<Page<Committee>> firstPage = committeeController.getCommittees(0, 5, "id", "asc");
        ResponseEntity<Page<Committee>> secondPage = committeeController.getCommittees(1, 5, "id", "asc");

        // then
        assertThat(firstPage.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(secondPage.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        if (!firstPage.getBody().getContent().isEmpty() && !secondPage.getBody().getContent().isEmpty()) {
            assertThat(firstPage.getBody().getContent().get(0).getId())
                    .isNotEqualTo(secondPage.getBody().getContent().get(0).getId());
        }
    }

    @Test
    @DisplayName("정렬 테스트 - ID 오름차순")
    void testSortByIdAsc() {
        // when
        ResponseEntity<Page<Committee>> response = committeeController.getCommittees(0, 10, "id", "asc");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        var committees = response.getBody().getContent();
        if (committees.size() > 1) {
            for (int i = 0; i < committees.size() - 1; i++) {
                assertThat(committees.get(i).getId()).isLessThanOrEqualTo(committees.get(i + 1).getId());
            }
        }
    }
}

