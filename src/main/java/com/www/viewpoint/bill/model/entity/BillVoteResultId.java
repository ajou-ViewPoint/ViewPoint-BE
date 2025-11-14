package com.www.viewpoint.bill.model.entity;


import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillVoteResultId implements Serializable {

    @Column(name = "bii_id")
    private String billId;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @Column(name = "party_name")
    private String partyName;
}