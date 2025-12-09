package com.www.viewpoint.assemblymember.service;

import com.www.viewpoint.assemblymember.model.dto.AssemblyMemberDto;
import com.www.viewpoint.config.DotenvInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = DotenvInitializer.class)
class AssemblyMemberServiceTest {

    @Autowired
    private AssemblyMemberService assemblyMemberService;

    @Test
    @DisplayName("keyword만으로 의원 필터링 테스트")
    void testFilterByKeyword() {
        // given
        String keyword = "홍";
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        Page<AssemblyMemberDto> result = assemblyMemberService.filterAssemblyMembers(
                keyword, null, null, page, size, sortBy, direction
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent())
                .allMatch(dto -> dto.getName().contains(keyword));
        
        // eraco, parties, committees가 채워져 있는지 확인
        result.getContent().forEach(dto -> {
            assertThat(dto.getEraco()).isNotNull();
            assertThat(dto.getParties()).isNotNull();
            assertThat(dto.getCommittees()).isNotNull();
        });
    }

    @Test
    @DisplayName("eraco만으로 의원 필터링 테스트")
    void testFilterByEraco() {
        // given
        String eraco = "제22대";
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        Page<AssemblyMemberDto> result = assemblyMemberService.filterAssemblyMembers(
                null, eraco, null, page, size, sortBy, direction
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        
        // eraco 리스트에 해당 대수가 포함되어 있는지 확인
        result.getContent().forEach(dto -> {
            assertThat(dto.getEraco()).isNotNull();
            assertThat(dto.getEraco()).contains(eraco);
            assertThat(dto.getParties()).isNotNull();
            assertThat(dto.getCommittees()).isNotNull();
        });
    }

    @Test
    @DisplayName("party만으로 의원 필터링 테스트")
    void testFilterByParty() {
        // given
        String party = "더불어민주당";
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        Page<AssemblyMemberDto> result = assemblyMemberService.filterAssemblyMembers(
                null, null, party, page, size, sortBy, direction
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        
        // parties 리스트에 해당 정당이 포함되어 있는지 확인
        result.getContent().forEach(dto -> {
            assertThat(dto.getParties()).isNotNull();
            assertThat(dto.getParties()).contains(party);
            assertThat(dto.getEraco()).isNotNull();
            assertThat(dto.getCommittees()).isNotNull();
        });
    }

    @Test
    @DisplayName("keyword와 eraco 조합 필터링 테스트")
    void testFilterByKeywordAndEraco() {
        // given
        String keyword = "홍";
        String eraco = "제22대";
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        Page<AssemblyMemberDto> result = assemblyMemberService.filterAssemblyMembers(
                keyword, eraco, null, page, size, sortBy, direction
        );

        // then
        assertThat(result).isNotNull();
        result.getContent().forEach(dto -> {
            assertThat(dto.getName()).contains(keyword);
            assertThat(dto.getEraco()).isNotNull();
            assertThat(dto.getEraco()).contains(eraco);
            assertThat(dto.getParties()).isNotNull();
            assertThat(dto.getCommittees()).isNotNull();
        });
    }

    @Test
    @DisplayName("keyword, eraco, party 모두 조합 필터링 테스트")
    void testFilterByAllParameters() {
        // given
        String keyword = "홍";
        String eraco = "제22대";
        String party = "더불어민주당";
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        Page<AssemblyMemberDto> result = assemblyMemberService.filterAssemblyMembers(
                keyword, eraco, party, page, size, sortBy, direction
        );

        // then
        assertThat(result).isNotNull();
        result.getContent().forEach(dto -> {
            assertThat(dto.getName()).contains(keyword);
            assertThat(dto.getEraco()).isNotNull();
            assertThat(dto.getEraco()).contains(eraco);
            assertThat(dto.getParties()).isNotNull();
            assertThat(dto.getParties()).contains(party);
            assertThat(dto.getCommittees()).isNotNull();
        });
    }

    @Test
    @DisplayName("필터 없이 전체 조회 테스트")
    void testFilterWithoutParameters() {
        // given
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        Page<AssemblyMemberDto> result = assemblyMemberService.filterAssemblyMembers(
                null, null, null, page, size, sortBy, direction
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getTotalElements()).isGreaterThan(0);
        
        // 모든 결과에 eraco, parties, committees가 채워져 있는지 확인
        result.getContent().forEach(dto -> {
            assertThat(dto.getEraco()).isNotNull();
            assertThat(dto.getParties()).isNotNull();
            assertThat(dto.getCommittees()).isNotNull();
        });
    }

    @Test
    @DisplayName("페이지네이션 테스트")
    void testPagination() {
        // given
        int page = 0;
        int size = 5;
        String sortBy = "id";
        String direction = "asc";

        // when
        Page<AssemblyMemberDto> firstPage = assemblyMemberService.filterAssemblyMembers(
                null, null, null, page, size, sortBy, direction
        );
        
        Page<AssemblyMemberDto> secondPage = assemblyMemberService.filterAssemblyMembers(
                null, null, null, page + 1, size, sortBy, direction
        );

        // then
        assertThat(firstPage).isNotNull();
        assertThat(secondPage).isNotNull();
        assertThat(firstPage.getContent().size()).isLessThanOrEqualTo(size);
        assertThat(secondPage.getContent().size()).isLessThanOrEqualTo(size);
        
        // 두 페이지의 결과가 다른지 확인
        if (!firstPage.getContent().isEmpty() && !secondPage.getContent().isEmpty()) {
            assertThat(firstPage.getContent().get(0).getMemberId())
                    .isNotEqualTo(secondPage.getContent().get(0).getMemberId());
        }
    }

    @Test
    @DisplayName("정렬 테스트 - 이름 오름차순")
    void testSortByNameAsc() {
        // given
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String direction = "asc";

        // when
        Page<AssemblyMemberDto> result = assemblyMemberService.filterAssemblyMembers(
                null, null, null, page, size, sortBy, direction
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent().size()).isGreaterThan(1);
        
        // 이름이 오름차순으로 정렬되었는지 확인
        for (int i = 0; i < result.getContent().size() - 1; i++) {
            String currentName = result.getContent().get(i).getName();
            String nextName = result.getContent().get(i + 1).getName();
            assertThat(currentName.compareTo(nextName)).isLessThanOrEqualTo(0);
        }
    }

    @Test
    @DisplayName("정렬 테스트 - ID 내림차순")
    void testSortByIdDesc() {
        // given
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "desc";

        // when
        Page<AssemblyMemberDto> result = assemblyMemberService.filterAssemblyMembers(
                null, null, null, page, size, sortBy, direction
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent().size()).isGreaterThan(1);
        
        // ID가 내림차순으로 정렬되었는지 확인
        for (int i = 0; i < result.getContent().size() - 1; i++) {
            Long currentId = result.getContent().get(i).getMemberId();
            Long nextId = result.getContent().get(i + 1).getMemberId();
            assertThat(currentId).isGreaterThanOrEqualTo(nextId);
        }
    }

    @Test
    @DisplayName("존재하지 않는 keyword로 필터링 시 빈 결과 반환 테스트")
    void testFilterByNonExistentKeyword() {
        // given
        String keyword = "존재하지않는이름12345";
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        Page<AssemblyMemberDto> result = assemblyMemberService.filterAssemblyMembers(
                keyword, null, null, page, size, sortBy, direction
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 eraco로 필터링 시 빈 결과 반환 테스트")
    void testFilterByNonExistentEraco() {
        // given
        String eraco = "제999대";
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        Page<AssemblyMemberDto> result = assemblyMemberService.filterAssemblyMembers(
                null, eraco, null, page, size, sortBy, direction
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("의원 상세 정보가 제대로 채워지는지 테스트")
    void testMemberDetailsAreFilled() {
        // given
        int page = 0;
        int size = 1;
        String sortBy = "id";
        String direction = "asc";

        // when
        Page<AssemblyMemberDto> result = assemblyMemberService.filterAssemblyMembers(
                null, null, null, page, size, sortBy, direction
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        
        AssemblyMemberDto dto = result.getContent().get(0);
        
        // 기본 필드 확인
        assertThat(dto.getMemberId()).isNotNull();
        assertThat(dto.getName()).isNotNull();
        assertThat(dto.getProfileImage()).isNotNull();
        
        // eraco, parties, committees 확인
        assertThat(dto.getEraco()).isNotNull();
        assertThat(dto.getParties()).isNotNull();
        assertThat(dto.getCommittees()).isNotNull();
        assertThat(dto.getElectionDistrict()).isNotNull();
    }
}

