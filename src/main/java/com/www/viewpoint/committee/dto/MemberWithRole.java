package com.www.viewpoint.committee.dto;

public interface MemberWithRole {
    Integer getMemberId();
    String getRole();

    // 의원 정보
    String getName();
    String getParty();
    String getProfileImage();
    String getNaasCode();
    Integer getAge();
}