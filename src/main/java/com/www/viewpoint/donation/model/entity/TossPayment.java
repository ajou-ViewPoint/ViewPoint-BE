package com.www.viewpoint.donation.model.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.www.viewpoint.donation.model.enums.TossPaymentMethod;
import com.www.viewpoint.donation.model.enums.TossPaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "toss_payment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TossPayment {

    @Id
    @Column(name = "payment_id", length = 255)
    private String paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "donation_id", nullable = false, unique = true)
    private Donation donation;

    @Enumerated(EnumType.STRING)
    @Column(name = "toss_payment_method", nullable = false, length = 30)
    private TossPaymentMethod tossPaymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "toss_payment_status", nullable = false, length = 30)
    private TossPaymentStatus tossPaymentStatus;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

}