package com.www.viewpoint.committee.dto;

import com.www.viewpoint.share.dto.AssemblyMemberSummaryDto;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MemberWithRole extends AssemblyMemberSummaryDto {
   String role;
}