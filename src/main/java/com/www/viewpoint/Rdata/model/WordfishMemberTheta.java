package com.www.viewpoint.Rdata.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wordfish_member_theta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordfishMemberTheta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String docId;
    private Double thetaRaw;
    private Integer age;             // 대수 → age

    private String committee;        // 위원회 이름 (예: "교육위원회")
    private String speakerName;      // 발언자
    private Long memberId;           // 의원ID

    private Double theta;
    private String party;
    private String displayName;      // label / 표시이름 통합

    private String sourceTag;        // 파일명 등 출처
}