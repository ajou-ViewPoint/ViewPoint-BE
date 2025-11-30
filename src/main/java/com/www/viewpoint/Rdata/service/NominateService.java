package com.www.viewpoint.Rdata.service;


import com.www.viewpoint.Rdata.model.NominatePointDto;
import com.www.viewpoint.Rdata.model.NominateResponseDto;
import com.www.viewpoint.Rdata.repository.NominateProjection;
import com.www.viewpoint.Rdata.repository.NominateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NominateService {

    private final NominateRepository nominateRepository;

    public List<NominateResponseDto> getNominateByAge(Integer age) {
        List<NominateProjection> rows = nominateRepository.findByAge(age);

        // age로 필터하면 의원당 1개라고 가정하지만,
        // 형식 맞추려고 data 리스트에 1개씩 담는다.
        return rows.stream()
                .map(row -> NominateResponseDto.builder()
                        .id(row.getMemberName())   // 한글 이름
                        .data(List.of(
                                NominatePointDto.builder()
                                        .x(row.getX())
                                        .y(row.getY())
                                        .party(row.getPartyName())
                                        .build()
                        ))
                        .build()
                )
                .collect(Collectors.toList());
    }
}
