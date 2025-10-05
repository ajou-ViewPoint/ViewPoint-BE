package com.www.viewpoint.party.service;

import com.www.viewpoint.party.model.entity.Party;
import com.www.viewpoint.party.respository.PartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PartyService {

    private final PartyRepository partyRepository;

    public PartyService(@Autowired PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    public Page<Party> getParties(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return partyRepository.findAll(pageable);
    }

    public Optional<Party> getPartyById(Integer id) {
        return partyRepository.findById(id);
    }
}

