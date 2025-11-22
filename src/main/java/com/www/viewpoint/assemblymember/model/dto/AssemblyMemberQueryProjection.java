package com.www.viewpoint.assemblymember.model.dto;

public interface AssemblyMemberQueryProjection {
    // AssemblyMemberSummaryDto
    Long getMemberId();
    String getName();
    String getParty();
    Integer getAge();
    String getDuty();
    String getProfileImage();
    String getDistrict();

    // AssemblyMemberDto 필드
    String getEngName();
    String getChName();
    String getBirthDate();
    String getGender();
    String getPhone();
    String getInnerDuty();
    String getHistory();
    Double getAttendanceRate();
    Double getLoyaltyRate();

}
