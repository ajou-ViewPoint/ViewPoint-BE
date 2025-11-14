package com.www.viewpoint.bill.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vote_bill_nass")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillVoteResult {

    @EmbeddedId
    private BillVoteResultId id;

    @Column(name = "nass_id")
    private Integer nassId;

    @Column(name = "vote_date")
    private java.sql.Date voteDate;

    @Column(name = "vote_opinion")
    private String voteOpinion; // 찬성 / 반대 / 기권 / 불참 등
}
