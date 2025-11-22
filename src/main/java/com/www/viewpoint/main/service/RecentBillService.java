package com.www.viewpoint.main.service;

import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.bill.repository.BillRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecentBillService {

    private final BillRepository billRepository;

    public RecentBillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

//    public List<Bill> getTop3RecentBills() {
//        return billRepository.findTop3ByProposeDtIsNotNullOrderByProposeDtDescIdDesc();
//    }

    public List<Bill> getTop3RecentBills() {
        return billRepository.findTop3ByRgsProcDateDesc(PageRequest.of(0, 3));
    }

}