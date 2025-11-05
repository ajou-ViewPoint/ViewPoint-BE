package com.www.viewpoint.bill.model.entity;

import com.www.viewpoint.assemblymember.model.entity.AssemblyMember;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bill_proposer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillProposer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bill_id", nullable = false, length = 100)
    private String billId;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "ch_name", length = 100)
    private String chName;

    @Column(name = "party_name", length = 100)
    private String partyName;

    @Column(name = "eraco", length = 50)
    private String eraco;

    @Column(name = "is_representative")
    private Boolean isRepresentative;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ✅ (선택) 연관관계 추가: member_id 외래키 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private AssemblyMember member;
}