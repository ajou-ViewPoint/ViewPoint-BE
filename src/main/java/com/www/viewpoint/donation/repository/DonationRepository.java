package com.www.viewpoint.donation.repository;

import com.www.viewpoint.donation.model.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DonationRepository extends JpaRepository<Donation, UUID> {
}
