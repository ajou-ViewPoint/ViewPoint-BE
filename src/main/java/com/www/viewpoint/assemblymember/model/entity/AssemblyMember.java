package com.www.viewpoint.assemblymember.model.entity;
import com.www.viewpoint.committee.model.entity.Committee;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "naas_committee",
            joinColumns = @JoinColumn(
                    name = "member_id",              // nass_committee.member_id
                    referencedColumnName = "id", // 이 엔티티의 member_id 컬럼
                    insertable = false,
                    updatable = false
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "committee_id"            // nass_committee.committee_id → Committee.id
            )
    )
    private List<Committee> committees;

    @Column(name = "party_id")
    private Integer partyId;

    @Column(name = "age")
    private Integer age;
}