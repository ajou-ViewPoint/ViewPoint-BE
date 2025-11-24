package com.www.viewpoint.main.dto;

import com.www.viewpoint.assemblymember.model.dto.AssemblyMemberQueryProjection;
import com.www.viewpoint.bill.model.entity.Bill;
import com.www.viewpoint.committee.model.entity.Committee;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GlobalSearchResponse {

    private List<Bill> bills;
    private List<AssemblyMemberQueryProjection> members;
    private List<Committee> committees;
}
