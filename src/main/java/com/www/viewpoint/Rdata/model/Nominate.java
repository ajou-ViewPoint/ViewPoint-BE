package com.www.viewpoint.Rdata.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nominate")
@Getter
@Setter
public class Nominate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "naas_code", nullable = false)
    private String naasCode;

    @Column(name = "age")
    private Integer age;

    @Column(name = "x")
    private Double x;

    @Column(name = "y")
    private Double y;

    @Column(name = "party_id")
    private Integer partyId;
}
