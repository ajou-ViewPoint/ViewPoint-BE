package com.www.viewpoint.constituency.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "winner_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WinnerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "eraco")
    private String eraco; // 제22대 등

    @Column(name = "sg_id")
    private String sgId; // 선거 ID

    @Column(name = "sgg_name")
    private String sggName; // 선거구명

    @Column(name = "sd_name")
    private String sdName; // 시도명 (서울특별시 등)

    @Column(name = "wiw_name")
    private String wiwName; // 시군구명 (강남구 등)

    @Column(name = "jd_name")
    private String jdName; // 정당명 (예: 더불어민주당)

    @Column(name = "name")
    private String name; // 후보자 이름

    @Column(name = "dugyul")
    private BigDecimal dugyul; // 득표율

    @Column(name = "party_id")
    private Long partyId;

    @Column(name = "member_id")
    private Long memberId;
    @Column(name = "region_id")
    private Long regionId;
}