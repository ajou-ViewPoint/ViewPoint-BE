package com.www.viewpoint.party.model.dto;


import com.www.viewpoint.share.dto.AssemblyMemberSummaryDto;

import java.util.List;

public record PartyMemberSummaryDto(
        Integer totalMembers,
        Integer proportionalCount,
        Integer districtCount,
        List<AssemblyMemberSummaryDto> members
) {}