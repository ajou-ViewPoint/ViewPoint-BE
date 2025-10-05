package com.www.viewpoint.committee.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "committee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Committee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "committee_code")
    private String committeeCode;

    @Column(name = "activities_description", columnDefinition = "TEXT")
    private String activitiesDescription;

    @Column(name = "schedule_info", columnDefinition = "TEXT")
    private String scheduleInfo;

    @Column(name = "committee_name", nullable = false)
    private String committeeName;
}

