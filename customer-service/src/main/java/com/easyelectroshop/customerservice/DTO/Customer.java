package com.easyelectroshop.customerservice.DTO;

import java.util.UUID;

public record Customer(
        UUID customerId,
        String fullName,
        String email,
        String phoneNumber,
        String gender,
        Address address,
        PaymentMethod paymentMethod,
        boolean profileComplete,
        boolean blocked,
        boolean active,
        int totalOrders,
        int totalOrderAmount
) {
}
