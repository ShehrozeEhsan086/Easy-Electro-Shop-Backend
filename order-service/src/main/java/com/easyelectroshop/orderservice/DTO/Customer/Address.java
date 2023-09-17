package com.easyelectroshop.orderservice.DTO.Customer;

public record Address(
        long addressId,
        String province,
        String city,
        String area,
        String addressLine,
        String postCode
) {
}
