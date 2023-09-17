package com.easyelectroshop.orderservice.DTO.Customer;

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
        boolean profileComplete,
        boolean blocked,
        boolean active,
        int totalOrders,
        double totalOrderAmount
) {
}
