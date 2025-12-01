package com.www.viewpoint.bill.model.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "bill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name="bill_id")
    private String billId;

    @Column(name = "bill_title", columnDefinition = "TEXT")
    private String billTitle;

    @Column(name = "proposer")
    private String proposer;

    @Column(name = "vote_tcnt")
    private Integer voteTcnt;

    @Column(name = "yes_tcnt")
    private Integer yesTcnt;

    @Column(name = "no_tcnt")
    private Integer noTcnt;

    @Column(name = "blank_tcnt")
    private Integer blankTcnt;

    @Column(name = "bill_summary", columnDefinition = "TEXT")
    private String billSummary;

    @Column(name = "proc_result_cd")
    private String procResultCd;

    @Column(name = "committee_submit_date")
    private LocalDate committeeSubmitDate;

    @Column(name = "committee_present_date")
    private LocalDate committeePresentDate;

    @Column(name = "committee_proc_date")
    private LocalDate committeeProcDate;

    @Column(name = "law_submit_date")
    private LocalDate lawSubmitDate;

    @Column(name = "law_present_date")
    private LocalDate lawPresentDate;

    @Column(name = "law_proc_date")
    private LocalDate lawProcDate;

    @Column(name = "rgs_present_date")
    private LocalDate rgsPresentDate;

    @Column(name = "rgs_proc_date", nullable = false)
    private LocalDate rgsProcDate;

    @Column(name = "propose_dt")
    private LocalDate proposeDt;

    @Column(name = "age")
    private Integer age;

    @Column(name = "announce_dt")
    private LocalDate announceDt;

    @Column(name = "curr_trans_dt")
    private LocalDate currTransDt;

    @Column(name="topics",columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> topics;
}
