package com.www.viewpoint.assemblymember.controller;

import com.www.viewpoint.assemblymember.model.dto.AssemblyMemberDto;
import com.www.viewpoint.bill.model.dto.BillSummaryDto;
import com.www.viewpoint.bill.model.dto.VoteSummaryByMemberResponse;
import com.www.viewpoint.config.DotenvInitializer;
import com.www.viewpoint.share.dto.AssemblyMemberSummaryDto;
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
class AssemblyMemberControllerTest {

    @Autowired
    private AssemblyMemberController assemblyMemberController;

    @Test
    @DisplayName("전체 국회의원 조회 테스트")
    void testGetAssemblyMembers() {
        // given
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "desc";

        // when
        ResponseEntity<Page<AssemblyMemberSummaryDto>> response = 
                assemblyMemberController.getAssemblyMembers(page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotEmpty();
        assertThat(response.getBody().getTotalElements()).isGreaterThan(0);
    }

    @Test
    @DisplayName("ID로 국회의원 조회 테스트")
    void testGetAssemblyMemberById() {
        // given
        Long id = 5799L; // 실제 존재하는 ID 사용

        // when
        ResponseEntity<AssemblyMemberDto> response = 
                assemblyMemberController.getAssemblyMemberById(id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMemberId()).isEqualTo(id);
        assertThat(response.getBody().getName()).isNotNull();
        
        // eraco, parties, committees가 채워져 있는지 확인
        assertThat(response.getBody().getEraco()).isNotNull();
        assertThat(response.getBody().getParties()).isNotNull();
        assertThat(response.getBody().getCommittees()).isNotNull();
    }

    @Test
    @DisplayName("대수별 국회의원 조회 테스트")
    void testGetAssemblyMembersByEraco() {
        // given
        String eraco = "제22대";
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        ResponseEntity<Page<AssemblyMemberSummaryDto>> response = 
                assemblyMemberController.getAssemblyMembersByEraco(eraco, page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("특정 의원이 발의한 법안 조회 테스트")
    void testGetBillsByMemberId() {
        // given
        Integer id = 1024; // 실제 존재하는 memberId 사용
        int page = 0;
        int size = 10;

        // when
        ResponseEntity<Page<BillSummaryDto>> response = 
                assemblyMemberController.getBillsByMemberId(id, page, size);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("특정 의원의 투표 요약 조회 테스트")
    void testGetVoteSummary() {
        // given
        Long memberId = 5397L; // 실제 존재하는 memberId 사용
        int page = 0;
        int size = 10;

        // when
        ResponseEntity<Page<VoteSummaryByMemberResponse>> response = 
                assemblyMemberController.getVoteSummary(memberId, page, size);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("복합 필터 - keyword만으로 의원 검색 테스트")
    void testFilterAssemblyMembersByKeyword() {
        // given
        String keyword = "홍";
        String eraco = null;
        String party = null;
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        ResponseEntity<Page<AssemblyMemberDto>> response = 
                assemblyMemberController.filterAssemblyMembers(keyword, eraco, party, page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotEmpty();
        
        // 모든 결과의 이름에 keyword가 포함되어 있는지 확인
        response.getBody().getContent().forEach(dto -> {
            assertThat(dto.getName()).contains(keyword);
            assertThat(dto.getEraco()).isNotNull();
            assertThat(dto.getParties()).isNotNull();
            assertThat(dto.getCommittees()).isNotNull();
        });
    }

    @Test
    @DisplayName("복합 필터 - eraco만으로 의원 검색 테스트")
    void testFilterAssemblyMembersByEraco() {
        // given
        String keyword = null;
        String eraco = "제22대";
        String party = null;
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        ResponseEntity<Page<AssemblyMemberDto>> response = 
                assemblyMemberController.filterAssemblyMembers(keyword, eraco, party, page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotEmpty();
        
        // 모든 결과의 eraco 리스트에 해당 대수가 포함되어 있는지 확인
        response.getBody().getContent().forEach(dto -> {
            assertThat(dto.getEraco()).isNotNull();
            assertThat(dto.getEraco()).contains(eraco);
            assertThat(dto.getParties()).isNotNull();
            assertThat(dto.getCommittees()).isNotNull();
        });
    }

    @Test
    @DisplayName("복합 필터 - party만으로 의원 검색 테스트")
    void testFilterAssemblyMembersByParty() {
        // given
        String keyword = null;
        String eraco = null;
        String party = "더불어민주당";
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        ResponseEntity<Page<AssemblyMemberDto>> response = 
                assemblyMemberController.filterAssemblyMembers(keyword, eraco, party, page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotEmpty();
        
        // 모든 결과의 parties 리스트에 해당 정당이 포함되어 있는지 확인
        response.getBody().getContent().forEach(dto -> {
            assertThat(dto.getParties()).isNotNull();
            assertThat(dto.getParties()).contains(party);
            assertThat(dto.getEraco()).isNotNull();
            assertThat(dto.getCommittees()).isNotNull();
        });
    }

    @Test
    @DisplayName("복합 필터 - keyword와 eraco 조합 검색 테스트")
    void testFilterAssemblyMembersByKeywordAndEraco() {
        // given
        String keyword = "홍";
        String eraco = "제22대";
        String party = null;
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        ResponseEntity<Page<AssemblyMemberDto>> response = 
                assemblyMemberController.filterAssemblyMembers(keyword, eraco, party, page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        response.getBody().getContent().forEach(dto -> {
            assertThat(dto.getName()).contains(keyword);
            assertThat(dto.getEraco()).isNotNull();
            assertThat(dto.getEraco()).contains(eraco);
            assertThat(dto.getParties()).isNotNull();
            assertThat(dto.getCommittees()).isNotNull();
        });
    }

    @Test
    @DisplayName("복합 필터 - 모든 파라미터 조합 검색 테스트")
    void testFilterAssemblyMembersByAllParameters() {
        // given
        String keyword = "홍";
        String eraco = "제22대";
        String party = "더불어민주당";
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        ResponseEntity<Page<AssemblyMemberDto>> response = 
                assemblyMemberController.filterAssemblyMembers(keyword, eraco, party, page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        response.getBody().getContent().forEach(dto -> {
            assertThat(dto.getName()).contains(keyword);
            assertThat(dto.getEraco()).isNotNull();
            assertThat(dto.getEraco()).contains(eraco);
            assertThat(dto.getParties()).isNotNull();
            assertThat(dto.getParties()).contains(party);
            assertThat(dto.getCommittees()).isNotNull();
        });
    }

    @Test
    @DisplayName("복합 필터 - 필터 없이 전체 조회 테스트")
    void testFilterAssemblyMembersWithoutFilters() {
        // given
        String keyword = null;
        String eraco = null;
        String party = null;
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        ResponseEntity<Page<AssemblyMemberDto>> response = 
                assemblyMemberController.filterAssemblyMembers(keyword, eraco, party, page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotEmpty();
        assertThat(response.getBody().getTotalElements()).isGreaterThan(0);
        
        // 모든 결과에 eraco, parties, committees가 채워져 있는지 확인
        response.getBody().getContent().forEach(dto -> {
            assertThat(dto.getEraco()).isNotNull();
            assertThat(dto.getParties()).isNotNull();
            assertThat(dto.getCommittees()).isNotNull();
        });
    }

    @Test
    @DisplayName("복합 필터 - 페이지네이션 테스트")
    void testFilterAssemblyMembersPagination() {
        // given
        int page = 0;
        int size = 5;
        String sortBy = "id";
        String direction = "asc";

        // when
        ResponseEntity<Page<AssemblyMemberDto>> firstPage = 
                assemblyMemberController.filterAssemblyMembers(null, null, null, page, size, sortBy, direction);
        
        ResponseEntity<Page<AssemblyMemberDto>> secondPage = 
                assemblyMemberController.filterAssemblyMembers(null, null, null, page + 1, size, sortBy, direction);

        // then
        assertThat(firstPage.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(secondPage.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(firstPage.getBody().getContent().size()).isLessThanOrEqualTo(size);
        assertThat(secondPage.getBody().getContent().size()).isLessThanOrEqualTo(size);
        
        // 두 페이지의 결과가 다른지 확인
        if (!firstPage.getBody().getContent().isEmpty() && !secondPage.getBody().getContent().isEmpty()) {
            assertThat(firstPage.getBody().getContent().get(0).getMemberId())
                    .isNotEqualTo(secondPage.getBody().getContent().get(0).getMemberId());
        }
    }

    @Test
    @DisplayName("복합 필터 - 정렬 테스트 (이름 오름차순)")
    void testFilterAssemblyMembersSortByNameAsc() {
        // given
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String direction = "asc";

        // when
        ResponseEntity<Page<AssemblyMemberDto>> response = 
                assemblyMemberController.filterAssemblyMembers(null, null, null, page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent().size()).isGreaterThan(1);
        
        // 이름이 오름차순으로 정렬되었는지 확인
        for (int i = 0; i < response.getBody().getContent().size() - 1; i++) {
            String currentName = response.getBody().getContent().get(i).getName();
            String nextName = response.getBody().getContent().get(i + 1).getName();
            assertThat(currentName.compareTo(nextName)).isLessThanOrEqualTo(0);
        }
    }

    @Test
    @DisplayName("복합 필터 - 정렬 테스트 (ID 내림차순)")
    void testFilterAssemblyMembersSortByIdDesc() {
        // given
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "desc";

        // when
        ResponseEntity<Page<AssemblyMemberDto>> response = 
                assemblyMemberController.filterAssemblyMembers(null, null, null, page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent().size()).isGreaterThan(1);
        
        // ID가 내림차순으로 정렬되었는지 확인
        for (int i = 0; i < response.getBody().getContent().size() - 1; i++) {
            Long currentId = response.getBody().getContent().get(i).getMemberId();
            Long nextId = response.getBody().getContent().get(i + 1).getMemberId();
            assertThat(currentId).isGreaterThanOrEqualTo(nextId);
        }
    }

    @Test
    @DisplayName("복합 필터 - 존재하지 않는 keyword로 검색 시 빈 결과 반환 테스트")
    void testFilterAssemblyMembersByNonExistentKeyword() {
        // given
        String keyword = "존재하지않는이름12345";
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "asc";

        // when
        ResponseEntity<Page<AssemblyMemberDto>> response = 
                assemblyMemberController.filterAssemblyMembers(keyword, null, null, page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isEmpty();
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("전체 의원 조회 - 기본 파라미터 테스트")
    void testGetAssemblyMembersWithDefaultParameters() {
        // when
        ResponseEntity<Page<AssemblyMemberSummaryDto>> response = 
                assemblyMemberController.getAssemblyMembers(0, 10, "id", "desc");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("대수별 의원 조회 - 기본 파라미터 테스트")
    void testGetAssemblyMembersByEracoWithDefaultParameters() {
        // given
        String eraco = "제22대";

        // when
        ResponseEntity<Page<AssemblyMemberSummaryDto>> response = 
                assemblyMemberController.getAssemblyMembersByEraco(eraco, 0, 10, "id", "asc");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}

