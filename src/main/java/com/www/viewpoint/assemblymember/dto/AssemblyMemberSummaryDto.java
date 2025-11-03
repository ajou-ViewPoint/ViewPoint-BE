package com.www.viewpoint.assemblymember.dto;

import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class AssemblyMemberSummaryDto {

    private Integer id;
    private String name;
    private String duty;

    public static AssemblyMemberSummaryDto fromEntity(AssemblyMember assemblyMember) {
        return AssemblyMemberSummaryDto.builder()
                .id(assemblyMember.getId())
                .name(assemblyMember.getName())
                .duty(assemblyMember.getDuty())
                .build();
    }
}