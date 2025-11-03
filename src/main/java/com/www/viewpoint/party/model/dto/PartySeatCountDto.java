package com.www.viewpoint.party.model.dto;

import java.util.List;

public record PartySeatCountDto(
        String partyName,
        Long partyId,
        Long totalSeats,
        String eraco
) {
    public static List<PartySeatCountDto> fromProjection(List<PartySeatCountProjection> projections) {
        if (projections == null || projections.isEmpty()) {
            return List.of();
        }

        return projections.stream()
                .map(p -> new PartySeatCountDto(
                        p.getPartyName(),
                        p.getPartyId(),
                        p.getTotalSeats(),
                        p.getEraco()
                ))
                .toList();
    }
}