package com.www.viewpoint.constituency.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "constituency")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Constituency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "const_name", nullable = false)
    private String constName;

    @Column(name = "estab_date")
    private LocalDate estabDate;

    @Column(name = "abolish_date")
    private LocalDate abolishDate;

    @Column(name = "const_code")
    private String constCode;
}

