package com.easyelectroshop.customerservice.DTO.OrderGetByIdResponse;

public record AddressResponse(
        String province,
        String city,
        String area,
        String addressLine,
        String postCode
) {
}