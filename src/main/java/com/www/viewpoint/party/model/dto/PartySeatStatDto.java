package com.www.viewpoint.party.model.dto;

import java.util.List;




public record PartySeatStatDto(
        Long totalSeats,
        List<PartySeatCountDto> partySeatStats
) {}