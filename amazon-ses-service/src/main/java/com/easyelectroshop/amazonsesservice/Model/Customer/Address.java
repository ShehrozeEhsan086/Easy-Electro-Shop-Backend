package com.easyelectroshop.amazonsesservice.Model.Customer;

public record Address(
        long addressId,
        String province,
        String city,
        String area,
        String addressLine,
        String postCode
) {

    @Override
    public String toString() {
        return "Address: "+addressLine+", "+area+", "+city+", "+province+", "+postCode;
    }
}
