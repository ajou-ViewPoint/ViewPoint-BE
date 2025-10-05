package com.www.viewpoint.bill.service;

import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.bill.respository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillService {

    private final BillRepository billRepository;

    public BillService(@Autowired BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public Page<Bill> getBills(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return billRepository.findAll(pageable);
    }

    public Optional<Bill> getBillById(Integer id) {
        return billRepository.findById(id);
    }





}
