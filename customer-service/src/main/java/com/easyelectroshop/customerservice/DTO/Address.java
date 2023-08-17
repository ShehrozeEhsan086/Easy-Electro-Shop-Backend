package com.easyelectroshop.customerservice.DTO;

public record Address(
        long addressId,
        String province,
        String city,
        String area,
        String addressLine,
        String postCode
) {
}
