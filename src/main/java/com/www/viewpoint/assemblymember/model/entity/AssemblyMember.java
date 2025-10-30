package com.www.viewpoint.assemblymember.model.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "national_assembly_member")
@Data // Getter, Setter, toString, equals, hashCode
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 전체 생성자
@Builder // Builder 패턴
public class AssemblyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "naas_code", nullable = false)
    private String naasCode;

    @Column(name = "profile_image", nullable = false, columnDefinition = "TEXT")
    private String profileImage;

    @Column(nullable = false)
    private String name;

    @Column(name = "eng_name", nullable = false)
    private String engName;

    @Column(name = "ch_name", nullable = false)
    private String chName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String eraco;

    @Column(nullable = false)
    private String duty;

    @Column(name = "inner_duty")
    private String innerDuty;

    @Column(name = "election_district")
    private String electionDistrict;

    @Column(name = "attendance_rate")
    private Double attendanceRate;

    @Column(name = "loyalty_rate")
    private Double loyaltyRate;

    @Column()
    private String party;

    @Column( columnDefinition = "TEXT")
    private String history;

    @Column(name = "committee_id")
    private Integer committeeId;

    @Column(name = "party_id")
    private Integer partyId;

    @Column(name = "age")
    private Integer age;
}