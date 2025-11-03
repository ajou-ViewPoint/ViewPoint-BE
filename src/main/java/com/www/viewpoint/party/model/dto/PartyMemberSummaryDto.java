package com.www.viewpoint.party.model.dto;


import java.util.List;

public record PartyMemberSummaryDto(
        Integer totalMembers,
        Integer proportionalCount,
        Integer districtCount,
        List<PartyMemberInfoProjection> members
) {}