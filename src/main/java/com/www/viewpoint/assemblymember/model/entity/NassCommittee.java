package com.www.viewpoint.assemblymember.model.entity;

import com.www.viewpoint.committee.model.entity.Committee;
import jakarta.persistence.*;

@Entity
@Table(name = "naas_committee")
public class NassCommittee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "committee_id")
    private Integer committeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private AssemblyMember member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "committee_id", insertable = false, updatable = false)
    private Committee committee;
}