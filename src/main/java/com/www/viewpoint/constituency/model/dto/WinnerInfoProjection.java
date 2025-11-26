package com.www.viewpoint.constituency.model.dto;

import java.math.BigDecimal;

public interface WinnerInfoProjection {
    Long getId();            // am.id
    String getName();        // wi.name
    String getParty();       // p.partyName
    Integer getAge();        // am.age
    String getEraco();       // am.eraco
    String getDuty();        // am.duty
    String getProfileImage(); // am.profileImage
    String getDistrict();     // wi.sggName (district 로 이름 지정)
    String getSidoName();     // kd.sidoName
    String getSggName();      // kd.sggName
    String getRegionCd();     // kd.code
    BigDecimal getVoteRate(); // wi.dugyul
}
