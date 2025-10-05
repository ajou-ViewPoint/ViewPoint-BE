package com.www.viewpoint.party.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "party")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "party_name", nullable = false)
    private String partyName;

    @Column(name = "founded_date")
    private LocalDate foundedDate;

    @Column(name = "dissolved_date")
    private LocalDate dissolvedDate;
}

