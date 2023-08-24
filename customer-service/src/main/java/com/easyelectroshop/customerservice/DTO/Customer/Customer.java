package com.easyelectroshop.customerservice.DTO.Customer;

import java.time.LocalDate;
import java.util.UUID;

public record Customer(
        UUID customerId,
        String fullName,
        String email,
        String phoneNumber,
        String gender,
        LocalDate dateOfBirth,
        Address address,
        PaymentMethod paymentMethod,
        boolean profileComplete,
        boolean blocked,
        boolean active,
        int totalOrders,
        int totalOrderAmount
) {
}
