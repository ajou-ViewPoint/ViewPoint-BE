package com.www.viewpoint.constituency.service;

import com.www.viewpoint.constituency.model.dto.WinnerInfoDto;
import com.www.viewpoint.constituency.model.entity.Constituency;
import com.www.viewpoint.constituency.repository.ConstituencyRepository;
import com.www.viewpoint.constituency.repository.WinnerInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConstituencyService {

    private final ConstituencyRepository constituencyRepository;

    private final WinnerInfoRepository winnerInfoRepository;


    @Autowired
    public ConstituencyService(
            ConstituencyRepository constituencyRepository,
            WinnerInfoRepository winnerInfoRepository
                                ) {
        this.constituencyRepository = constituencyRepository;
        this.winnerInfoRepository =winnerInfoRepository;
    }

    public List<WinnerInfoDto> findMembersByRegion(String sido, String gungu, String eracos) {
        return winnerInfoRepository.findMembersByRegion(sido, gungu, eracos);
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

