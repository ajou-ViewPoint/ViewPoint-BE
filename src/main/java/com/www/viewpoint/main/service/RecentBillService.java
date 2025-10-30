package com.www.viewpoint.main.service;

import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.bill.repository.BillRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecentBillService {

    private final BillRepository billRepository;

    public RecentBillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public List<Bill> getTop3RecentBills() {
        List<Bill> allSorted = billRepository.findByProposeDtIsNotNullOrderByProposeDtDescIdDesc();
        if (allSorted.isEmpty()) {
            return List.of();
        }

        List<Bill> picked = new ArrayList<>(3);

        for (Bill bill : allSorted) {
            if (bill.getProposeDt() == null) {
                continue;
            }
            picked.add(bill);
            if (picked.size() >= 3) {
                break;
            }
        }

        return picked;
    }
}