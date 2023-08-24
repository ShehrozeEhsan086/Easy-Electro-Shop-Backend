package com.easyelectroshop.customermanagementservice.DTO;

import com.easyelectroshop.customermanagementservice.Model.Address;

import java.util.UUID;

public record CustomerDTO(UUID customerId,
                          String fullName,
                          String email,
                          String phoneNumber,
                          String gender,
                          Address address,
                          boolean profileComplete,
                          boolean blocked,
                          boolean active,
                          int totalOrders,
                          int totalOrderAmount) {
}
