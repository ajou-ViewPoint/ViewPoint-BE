package com.www.viewpoint.donation.service;

import com.www.viewpoint.donation.model.dto.DonationRequest;
import com.www.viewpoint.donation.model.entity.Donation;
import com.www.viewpoint.donation.model.entity.TossPayment;
import com.www.viewpoint.donation.repository.DonationRepository;
import com.www.viewpoint.donation.repository.TossPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class DonationService {

    private final TossPaymentRepository tossPaymentRepository;

    private final DonationRepository donationRepository;


    public Donation createDonation(@RequestBody DonationRequest donationRequest) {
        Donation donation = Donation.builder()
                .donorName(donationRequest.getDonorName())
                .donorEmail(donationRequest.getDonorEmail())
                .message(donationRequest.getMessage())
                .amount(donationRequest.getAmount())
                .build();
        donation = donationRepository.save(donation);
        return donation;
    }

    public Donation findById(UUID id) {
        return donationRepository.findById(id).orElse(null);
    }

    public void deleteById(UUID id) {
        donationRepository.deleteById(id);
    }
    @Transactional
    public TossPayment attachPaymentToDonation(UUID donationId, TossPayment tossPayment) {
        // 1️⃣ Donation 조회
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found for ID: " + donationId));

        // 2️⃣ 양방향 매핑 설정
        tossPayment.setDonation(donation);
        donation.setTossPayment(tossPayment);

        // 3️⃣ Donation 업데이트 (예: 후원 완료 시각 갱신)
        donation.setUpdatedAt(LocalDateTime.now());

        // 4️⃣ 저장 (CascadeType.ALL로 인해 Donation 저장 시 TossPayment도 자동 저장 가능)
        donationRepository.save(donation);

        // 5️⃣ 또는 TossPayment를 직접 저장
        tossPaymentRepository.save(tossPayment);

        return tossPayment;
    }

    @Transactional(readOnly = true)
    public List<Donation> findAllDonations() {
        return donationRepository.findAll();
    }


}
