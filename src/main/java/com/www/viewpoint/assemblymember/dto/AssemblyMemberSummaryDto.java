package com.www.viewpoint.assemblymember.dto;

import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AssemblyMemberSummaryDto {

    private Long id;
    private String name;
    private String party;
    private Integer age;
    private String duty;
    private String profileImage;
    public static AssemblyMemberSummaryDto fromEntity(AssemblyMember assemblyMember) {
        return AssemblyMemberSummaryDto.builder()
                .id(assemblyMember.getId().longValue())
                .name(assemblyMember.getName())
                .party(assemblyMember.getParty())
                .profileImage(assemblyMember.getProfileImage())
                .build();
    }
}