package com.easyelectroshop.customerservice.DTO;

public record PaymentMethod(
        long paymentOptionId,
        String cardHolderName,
        String cardNumber,
        String expiryDate,
        String cvv
) {
}
