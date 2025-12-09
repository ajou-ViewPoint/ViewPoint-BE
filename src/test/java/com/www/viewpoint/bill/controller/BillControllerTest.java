package com.www.viewpoint.bill.controller;

import com.www.viewpoint.bill.model.entity.Bill;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ContextConfiguration(initializers = DotenvInitializer.class)
class BillControllerTest {

    @Autowired
    private BillController billController;

    @Test
    @DisplayName("전체 법안 조회 테스트")
    void testGetBills() {
        // given
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String direction = "desc";

        // when
        ResponseEntity<Page<Bill>> response = billController.getBills(page, size, sortBy, direction);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("ID로 법안 조회 테스트")
    void testGetBillById() {
        // given - 먼저 전체 조회해서 실제 존재하는 billId 가져오기
        ResponseEntity<Page<Bill>> allBills = billController.getBills(0, 1, "id", "desc");
        assertThat(allBills.getBody()).isNotNull();
        assertThat(allBills.getBody().getContent()).isNotEmpty();
        
        String billId = allBills.getBody().getContent().get(0).getBillId();

        // when
        ResponseEntity<Bill> response = billController.getBillById(billId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBillId()).isEqualTo(billId);
    }

    @Test
    @DisplayName("키워드 기반 법안 검색 테스트")
    void testSearchBillsByKeyword() {
        // given
        String keyword = "의료";

        // when
        ResponseEntity<List<Bill>> response = billController.searchBillsByKeyword(keyword);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("발의일 범위로 법안 검색 테스트")
    void testSearchBillsByDateRange() {
        // given
        String startStr = "2024-01-01";
        String endStr = "2024-12-31";

        // when
        ResponseEntity<List<Bill>> response = billController.searchBillsByDateRange(startStr, endStr);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("복합 필터 기반 법안 검색 - keyword만")
    void testFilterBillsByKeyword() {
        // when
        ResponseEntity<Page<Bill>> response = billController.filterBills(
                "의료", null, null, null, null, null, 0, 10, "proposeDt", "desc"
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("복합 필터 기반 법안 검색 - age만")
    void testFilterBillsByAge() {
        // when
        ResponseEntity<Page<Bill>> response = billController.filterBills(
                null, null, null, 22, null, null, 0, 10, "proposeDt", "desc"
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        if (!response.getBody().getContent().isEmpty()) {
            response.getBody().getContent().forEach(bill -> {
                assertThat(bill.getAge()).isEqualTo(22);
            });
        }
    }

    @Test
    @DisplayName("복합 필터 기반 법안 검색 - party만")
    void testFilterBillsByParty() {
        // when
        ResponseEntity<Page<Bill>> response = billController.filterBills(
                null, null, null, null, "더불어민주당", null, 0, 10, "proposeDt", "desc"
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("복합 필터 기반 법안 검색 - procResultCd만")
    void testFilterBillsByProcResultCd() {
        // when
        ResponseEntity<Page<Bill>> response = billController.filterBills(
                null, null, null, null, null, "원안가결", 0, 10, "proposeDt", "desc"
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        if (!response.getBody().getContent().isEmpty()) {
            response.getBody().getContent().forEach(bill -> {
                assertThat(bill.getProcResultCd()).isEqualTo("원안가결");
            });
        }
    }

    @Test
    @DisplayName("복합 필터 기반 법안 검색 - 잘못된 procResultCd")
    void testFilterBillsByInvalidProcResultCd() {
        // when
        ResponseEntity<Page<Bill>> response = billController.filterBills(
                null, null, null, null, null, "잘못된코드", 0, 10, "proposeDt", "desc"
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("페이지네이션 테스트")
    void testPagination() {
        // when
        ResponseEntity<Page<Bill>> firstPage = billController.getBills(0, 5, "id", "asc");
        ResponseEntity<Page<Bill>> secondPage = billController.getBills(1, 5, "id", "asc");

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
        ResponseEntity<Page<Bill>> response = billController.getBills(0, 10, "id", "desc");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        List<Bill> bills = response.getBody().getContent();
        if (bills.size() > 1) {
            for (int i = 0; i < bills.size() - 1; i++) {
                assertThat(bills.get(i).getId()).isGreaterThanOrEqualTo(bills.get(i + 1).getId());
            }
        }
    }
}

