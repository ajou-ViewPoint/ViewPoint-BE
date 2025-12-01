package com.www.viewpoint.assemblymember.model.entity;
import com.www.viewpoint.committee.model.entity.Committee;
import com.www.viewpoint.party.model.entity.Party;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "assembly_member_eraco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssemblyMemberEraco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "eraco", nullable = false, length = 20)
    private String eraco; // 예: "제22대"

    @Column(name = "constituency_type", length = 255)
    private String constituencyType; // 예: "지역구" / "비례대표"

    @Column(name = "election_district", length = 255)
    private String electionDistrict; // 예: "서울 종로구"

    @Column(name = "party_id")
    private Integer partyId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "election_district_id")
    private Integer electionDistrictId;

    @Column(name = "committee_id")
    private Integer committeeId;

    @Column(name="age")
    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "committee_id", insertable = false, updatable = false)
    private Committee committee;

    // ✅ 의원 정보 연관관계 (선택)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private AssemblyMember member;

    // ✅ 정당 정보 연관관계 (선택)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", insertable = false, updatable = false)
    private Party party;
}