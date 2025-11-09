package com.www.viewpoint.donation.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "donation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Donation {

    @Id
    @Column(name = "donation_id", columnDefinition = "BINARY(16)")
    private UUID donationId;

    @Column(name = "donor_name", nullable = false, length = 100)
    private String donorName;

    @Column(name = "donor_email", length = 255)
    private String donorEmail;

    @Column(name = "message", length = 255)
    private String message;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "donation", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    @JsonManagedReference
    private TossPayment tossPayment;

    @PrePersist
    public void generateIdIfAbsent() {
        if (this.donationId == null) {
            this.donationId = UUID.randomUUID();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

}