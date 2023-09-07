package com.easyelectroshop.stripepaymentservice.DTO;

public class CreatePaymentResponse {
    private String clientSecret;

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public CreatePaymentResponse(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}