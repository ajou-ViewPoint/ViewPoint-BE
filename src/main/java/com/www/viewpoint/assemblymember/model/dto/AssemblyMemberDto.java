package com.www.viewpoint.assemblymember.model.dto;
import com.www.viewpoint.committee.model.entity.Committee;
import com.www.viewpoint.share.dto.AssemblyMemberSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AssemblyMemberDto  {
    private Long memberId;
    private String name;
    private String duty;
    private String profileImage;
    private String district;
    String engName;
    String chName;
    String birthDate;
    List<String> electionDistrict;
    List<String> eraco;
    List<String> parties;
    List<Committee> committees;
    String gender;
    String phone;
    String innerDuty;
    String history;
    Double attendanceRate;
    Double loyaltyRate;
}
