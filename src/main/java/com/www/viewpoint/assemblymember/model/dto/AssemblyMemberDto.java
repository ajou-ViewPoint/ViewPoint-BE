package com.www.viewpoint.assemblymember.model.dto;
import com.www.viewpoint.share.dto.AssemblyMemberSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AssemblyMemberDto extends AssemblyMemberSummaryDto {
    String engName;
    String chName;
    String birthDate;
    String electionDistrict;
    String eraco;
    String gender;
    String phone;
    String innerDuty;
    String history;
    Double attendanceRate;
    Double loyaltyRate;
}
