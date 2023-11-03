package com.easyelectroshop.analyticsservice.DTO.Customer;

import java.time.LocalDate;
import java.util.UUID;

public record Customer(
        UUID customerId,
        String fullName,
        String email,
        String phoneNumber,
        String gender,
        String dateOfBirth,
        Address address,
        boolean profileComplete,
        boolean blocked,
        boolean active,
        int totalOrders,
        double totalOrderAmount
) {
}
