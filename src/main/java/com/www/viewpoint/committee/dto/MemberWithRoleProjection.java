package com.www.viewpoint.committee.dto;

public interface MemberWithRoleProjection {
    Long getMemberId();
    String getRole();
    String getName();
    Integer getAge();
    String getDuty();
    String getPartyName();
    String getElectionDistrict();
    String getProfileImage();
}