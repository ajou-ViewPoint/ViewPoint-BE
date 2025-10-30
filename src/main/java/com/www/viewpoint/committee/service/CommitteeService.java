package com.www.viewpoint.committee.service;

import com.www.viewpoint.committee.model.entity.Committee;
import com.www.viewpoint.committee.repository.CommitteeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommitteeService {

    private final CommitteeRepository committeeRepository;

    public CommitteeService(@Autowired CommitteeRepository committeeRepository) {
        this.committeeRepository = committeeRepository;
    }

    public Page<Committee> getCommittees(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return committeeRepository.findAll(pageable);
    }

    public Optional<Committee> getCommitteeById(Integer id) {
        return committeeRepository.findById(id);
    }
}

