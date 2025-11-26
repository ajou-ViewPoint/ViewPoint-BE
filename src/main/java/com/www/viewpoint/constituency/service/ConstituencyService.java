package com.www.viewpoint.constituency.service;

import com.www.viewpoint.constituency.model.dto.WinnerInfoDto;
import com.www.viewpoint.constituency.model.dto.WinnerInfoProjection;
import com.www.viewpoint.constituency.model.entity.Constituency;
import com.www.viewpoint.constituency.model.entity.KoreaDistrict;
import com.www.viewpoint.constituency.repository.ConstituencyRepository;
import com.www.viewpoint.constituency.repository.DistrictRepository;
import com.www.viewpoint.constituency.repository.WinnerInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConstituencyService {
    private static final Logger log = LoggerFactory.getLogger(ConstituencyService.class);

    private final ConstituencyRepository constituencyRepository;

    private final WinnerInfoRepository winnerInfoRepository;

    private final DistrictRepository  districtRepository;



    /**
     * 1. 위경도로 boundary 내부 지역구 탐색 + 해당 지역구 의원 반환
     */
    public List<WinnerInfoProjection> findMembersByCoords(double lon, double lat) {
        KoreaDistrict district = districtRepository.findDistrictByCoordinates(lon, lat)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌표의 행정구역을 찾을 수 없습니다."));

        district= districtRepository.findSggByCodePrefix(district.getCode().substring(0, 5))
                .orElseThrow(() -> new IllegalArgumentException("해당 코드의 행정코드를 찾을 수 없습니다."));
//        log.info(district.getSggName());
        return winnerInfoRepository.findWinnerByRegionId(district.getId());
    }

    public Page<Constituency> getConstituencies(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return constituencyRepository.findAll(pageable);
    }

    /**
     * 2. sido, sgg, code 기반으로 의원 검색
     */
    public List<WinnerInfoProjection> findMembersByRegion(String sido, String sgg, String code) {
        return winnerInfoRepository.findMembersByRegion(sido, sgg, code);
    }

    public Optional<Constituency> getConstituencyById(Integer id) {
        return constituencyRepository.findById(id);
    }

    public String getRandomDistrictCode() {
        KoreaDistrict district = districtRepository.findRandomSggCode()
                .orElseThrow(() -> new IllegalStateException("5자리 지역 코드가 없습니다."));

        return district.getCode();  // 여기서 code만 리턴!
    }
}

