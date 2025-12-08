package com.www.viewpoint.committee.service;

import com.www.viewpoint.committee.model.entity.Committee;
import com.www.viewpoint.config.DotenvInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = DotenvInitializer.class)
class CommitteeServiceTest {

    @Autowired
    private CommitteeService committeeService;

    @Test
    @DisplayName("전체 위원회 조회 테스트")
    void testGetCommittees() {
        // when
        Page<Committee> result = committeeService.getCommittees(0, 10, "id", "desc");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("ID로 위원회 조회 테스트")
    void testGetCommitteeById() {
        // given - 전체 조회해서 실제 ID 가져오기
        Page<Committee> all = committeeService.getCommittees(0, 1, "id", "asc");
        assertThat(all.getContent()).isNotEmpty();
        Integer id = all.getContent().get(0).getId();

        // when
        Optional<Committee> result = committeeService.getCommitteeById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional 반환")
    void testGetCommitteeByIdNotFound() {
        // when
        Optional<Committee> result = committeeService.getCommitteeById(999999);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("위원회 멤버 및 통계 조회 테스트")
    void testGetCommitteeMembersAndStats() {
        // given - 전체 조회해서 실제 ID 가져오기
        Page<Committee> all = committeeService.getCommittees(0, 1, "id", "asc");
        assertThat(all.getContent()).isNotEmpty();
        Integer id = all.getContent().get(0).getId();

        // when
        Optional<Map<String, Object>> result = committeeService.getCommitteeMembersAndStats(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).containsKey("committeeName");
        assertThat(result.get()).containsKey("committeeId");
        assertThat(result.get()).containsKey("membersByRole");
        assertThat(result.get()).containsKey("stats");
    }

    @Test
    @DisplayName("페이지네이션 테스트")
    void testPagination() {
        // when
        Page<Committee> firstPage = committeeService.getCommittees(0, 5, "id", "asc");
        Page<Committee> secondPage = committeeService.getCommittees(1, 5, "id", "asc");

        // then
        assertThat(firstPage).isNotNull();
        assertThat(secondPage).isNotNull();
        
        if (!firstPage.getContent().isEmpty() && !secondPage.getContent().isEmpty()) {
            assertThat(firstPage.getContent().get(0).getId())
                    .isNotEqualTo(secondPage.getContent().get(0).getId());
        }
    }

    @Test
    @DisplayName("정렬 테스트 - committeeName 오름차순")
    void testSortByNameAsc() {
        // when
        Page<Committee> result = committeeService.getCommittees(0, 10, "committeeName", "asc");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }
}

