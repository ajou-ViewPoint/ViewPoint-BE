package com.www.viewpoint.party.service;

import com.www.viewpoint.config.DotenvInitializer;
import com.www.viewpoint.party.model.dto.PartyMemberSummaryDto;
import com.www.viewpoint.party.model.dto.PartySeatStatDto;
import com.www.viewpoint.party.model.entity.Party;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = DotenvInitializer.class)
class PartyServiceTest {

    @Autowired
    private PartyService partyService;

    @Test
    @DisplayName("전체 정당 조회 테스트")
    void testGetParties() {
        // when
        Page<Party> result = partyService.getParties(0, 10, "id", "desc");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("ID로 정당 조회 테스트")
    void testGetPartyById() {
        // given - 전체 조회해서 실제 ID 가져오기
        Page<Party> all = partyService.getParties(0, 1, "id", "asc");
        assertThat(all.getContent()).isNotEmpty();
        Integer id = all.getContent().get(0).getId();

        // when
        Optional<Party> result = partyService.getPartyById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional 반환")
    void testGetPartyByIdNotFound() {
        // when
        Optional<Party> result = partyService.getPartyById(999999);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("정당별 의원 요약 정보 조회 테스트")
    void testGetPartyMemberSummary() {
        // given
        String partyName = "더불어민주당";
        String eraco = "제22대";

        // when
        PartyMemberSummaryDto result = partyService.getPartyMemberSummary(partyName, eraco);

        // then
        assertThat(result).isNotNull();
        assertThat(result.totalMembers()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 정당 의원 요약 정보 조회 시 빈 결과 반환")
    void testGetPartyMemberSummaryNonExistent() {
        // given
        String partyName = "존재하지않는정당12345";
        String eraco = "제22대";

        // when
        PartyMemberSummaryDto result = partyService.getPartyMemberSummary(partyName, eraco);

        // then
        assertThat(result).isNotNull();
        assertThat(result.totalMembers()).isEqualTo(0);
        assertThat(result.members()).isEmpty();
    }

    @Test
    @DisplayName("정당별 의석 통계 조회 테스트")
    void testGetPartySeatsByEraco() {
        // given
        String eraco = "제22대";

        // when
        PartySeatStatDto result = partyService.getPartySeatsByErcao(eraco);

        // then
        assertThat(result).isNotNull();
        assertThat(result.totalSeats()).isGreaterThan(0);
        assertThat(result.partySeatStats()).isNotEmpty();
    }

    @Test
    @DisplayName("페이지네이션 테스트")
    void testPagination() {
        // when
        Page<Party> firstPage = partyService.getParties(0, 5, "id", "asc");
        Page<Party> secondPage = partyService.getParties(1, 5, "id", "asc");

        // then
        assertThat(firstPage).isNotNull();
        assertThat(secondPage).isNotNull();
        
        if (!firstPage.getContent().isEmpty() && !secondPage.getContent().isEmpty()) {
            assertThat(firstPage.getContent().get(0).getId())
                    .isNotEqualTo(secondPage.getContent().get(0).getId());
        }
    }

    @Test
    @DisplayName("정렬 테스트 - partyName 오름차순")
    void testSortByNameAsc() {
        // when
        Page<Party> result = partyService.getParties(0, 10, "partyName", "asc");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }
}

