package com.www.viewpoint.donation.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class TossPaymentConfig {

    @Value("${payment.toss.secret-key}")
    private String secretKey;

    private static final String BASE_URL = "https://api.tosspayments.com/v1";

    public String getPaymentConfirmUrl() {
        return BASE_URL + "/payments/confirm";
    }

    public String getBillingIssueUrl() {
        return BASE_URL + "/billing/authorizations/issue";
    }

    public String getBillingConfirmUrl(String billingKey) {
        return BASE_URL + "/billing/" + billingKey;
    }

    public String getBrandpayConfirmUrl() {
        return BASE_URL + "/brandpay/payments/confirm";
    }

    public String getBrandpayAuthUrl() {
        return BASE_URL + "/brandpay/authorizations/access-token";
    }
}
