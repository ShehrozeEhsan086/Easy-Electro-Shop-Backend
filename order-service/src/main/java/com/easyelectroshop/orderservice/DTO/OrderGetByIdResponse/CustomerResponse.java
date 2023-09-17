package com.easyelectroshop.orderservice.DTO.OrderGetByIdResponse;


public record CustomerResponse(
        String fullName,
        String email,
        String phoneNumber,
        String gender,
        AddressResponse address
) {
}
