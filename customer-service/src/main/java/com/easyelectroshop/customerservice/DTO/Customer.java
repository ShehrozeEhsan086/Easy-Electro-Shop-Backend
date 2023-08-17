package com.easyelectroshop.customerservice.DTO;

import java.util.UUID;

public record Customer(
        UUID customerId,
        String userName,
        String fullName,
        String email,
        String phoneNumber,
        String gender,
        Address address,
        PaymentMethod paymentMethod,
        boolean isProfileComplete,
        boolean isBlocked,
        boolean isActive,
        int totalOrders,
        int totalOrderAmount
) {
}
