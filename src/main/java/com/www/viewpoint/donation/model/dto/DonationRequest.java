package com.www.viewpoint.donation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonationRequest {

    private String donorName;
    private String donorEmail;
    private Long amount;
    private String message;
}
