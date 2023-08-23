package com.easyelectroshop.customerservice.DTO.Customer;

public record PaymentMethod(
        long paymentOptionId,
        String cardHolderName,
        String cardNumber,
        String expiryDate,
        String cvv
) {
}
