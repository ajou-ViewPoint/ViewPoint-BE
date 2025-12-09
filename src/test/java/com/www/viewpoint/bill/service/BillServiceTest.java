package com.www.viewpoint.bill.service;

import com.www.viewpoint.bill.model.dto.BillVoteSummaryDto;
import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.config.DotenvInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = DotenvInitializer.class)
class BillServiceTest {

    @Autowired
    private BillService billService;

    @Test
    @DisplayName("전체 법안 조회 테스트")
    void testGetBills() {
        // when
        Page<Bill> result = billService.getBills(0, 10, "id", "desc");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("키워드로 법안 검색 테스트")
    void testSearchBillsByKeyword() {
        // given
        String keyword = "의료";

        // when
        List<Bill> result = billService.searchBillsByKeyword(keyword);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("빈 키워드로 검색 시 빈 결과 반환")
    void testSearchBillsByEmptyKeyword() {
        // when
        List<Bill> result = billService.searchBillsByKeyword("");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("날짜 범위로 법안 검색 테스트")
    void testSearchBillsByDateRange() {
        // given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        // when
        List<Bill> result = billService.searchBillsByDateRange(startDate, endDate);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("날짜 범위 검색 - 시작일만 제공")
    void testSearchBillsByDateRangeStartOnly() {
        // given
        LocalDate startDate = LocalDate.of(2024, 1, 1);

        // when
        List<Bill> result = billService.searchBillsByDateRange(startDate, null);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("날짜 범위 검색 - 종료일만 제공")
    void testSearchBillsByDateRangeEndOnly() {
        // given
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        // when
        List<Bill> result = billService.searchBillsByDateRange(null, endDate);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("날짜 범위 검색 - 둘 다 null")
    void testSearchBillsByDateRangeBothNull() {
        // when
        List<Bill> result = billService.searchBillsByDateRange(null, null);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("복합 필터 검색 - keyword만")
    void testSearchBillsWithFiltersKeywordOnly() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "proposeDt"));

        // when
        Page<Bill> result = billService.searchBillsWithFilters(
                "의료", null, null, null, null, null, pageable
        );

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("복합 필터 검색 - age만")
    void testSearchBillsWithFiltersAgeOnly() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "proposeDt"));

        // when
        Page<Bill> result = billService.searchBillsWithFilters(
                null, null, null, 22, null, null, pageable
        );

        // then
        assertThat(result).isNotNull();
        if (!result.getContent().isEmpty()) {
            result.getContent().forEach(bill -> {
                assertThat(bill.getAge()).isEqualTo(22);
            });
        }
    }

    @Test
    @DisplayName("복합 필터 검색 - procResultCd만")
    void testSearchBillsWithFiltersProcResultCdOnly() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "proposeDt"));

        // when
        Page<Bill> result = billService.searchBillsWithFilters(
                null, null, null, null, null, "원안가결", pageable
        );

        // then
        assertThat(result).isNotNull();
        if (!result.getContent().isEmpty()) {
            result.getContent().forEach(bill -> {
                assertThat(bill.getProcResultCd()).isEqualTo("원안가결");
            });
        }
    }

    @Test
    @DisplayName("복합 필터 검색 - 모든 필터 조합")
    void testSearchBillsWithAllFilters() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "proposeDt"));
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        // when
        Page<Bill> result = billService.searchBillsWithFilters(
                "법", startDate, endDate, 22, null, null, pageable
        );

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("복합 필터 검색 - 존재하지 않는 정당")
    void testSearchBillsWithFiltersNonExistentParty() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "proposeDt"));

        // when
        Page<Bill> result = billService.searchBillsWithFilters(
                null, null, null, null, "존재하지않는정당12345", null, pageable
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("법안 투표 결과 조회 테스트")
    void testGetBillVoteResult() {
        // given - 실제 존재하는 billId 사용
        Page<Bill> bills = billService.getBills(0, 1, "id", "desc");
        if (bills.getContent().isEmpty()) return;
        
        String billId = bills.getContent().get(0).getBillId();

        // when
        BillVoteSummaryDto result = billService.getBillVoteResult(billId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAgree()).isNotNull();
        assertThat(result.getDisagree()).isNotNull();
        assertThat(result.getAbstain()).isNotNull();
        assertThat(result.getAbsent()).isNotNull();
    }
}

