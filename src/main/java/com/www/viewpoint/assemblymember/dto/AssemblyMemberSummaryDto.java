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
    private String party;
    private String duty;
    private String profileImage;
    public static AssemblyMemberSummaryDto fromEntity(AssemblyMember assemblyMember) {
        return AssemblyMemberSummaryDto.builder()
                .id(assemblyMember.getId())
                .name(assemblyMember.getName())
                .party(assemblyMember.getParty())
                .profileImage(assemblyMember.getProfileImage())
                .build();
    }
}