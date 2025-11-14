package com.www.viewpoint.constituency.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

@Entity
@Table(name = "korea_districts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KoreaDistrict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 행정구역 코드 */
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    /** 시도명 (예: 서울특별시 / 경기도 / 전북특별자치도) */
    @Column(name = "sido_name")
    private String sidoName;

    /** 시군구명 (예: 중구, 강남구, 완주군 등) */
    @Column(name = "sgg_name")
    private String sggName;


}
