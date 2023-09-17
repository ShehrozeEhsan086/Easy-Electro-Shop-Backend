package com.easyelectroshop.customerservice.DTO.OrderGetByIdResponse;

import com.easyelectroshop.customerservice.DTO.Customer.Address;

public record CustomerResponse(
        String fullName,
        String email,
        String phoneNumber,
        String gender,
        AddressResponse address
) {
}
