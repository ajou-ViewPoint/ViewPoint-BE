package com.www.viewpoint.constituency.service;

import com.www.viewpoint.constituency.model.entity.Constituency;
import com.www.viewpoint.constituency.respository.ConstituencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConstituencyService {

    private final ConstituencyRepository constituencyRepository;

    public ConstituencyService(@Autowired ConstituencyRepository constituencyRepository) {
        this.constituencyRepository = constituencyRepository;
    }

    public Page<Constituency> getConstituencies(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return constituencyRepository.findAll(pageable);
    }

    public Optional<Constituency> getConstituencyById(Integer id) {
        return constituencyRepository.findById(id);
    }
}

