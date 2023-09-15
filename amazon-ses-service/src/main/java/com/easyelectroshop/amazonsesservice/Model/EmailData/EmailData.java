package com.easyelectroshop.amazonsesservice.Model.EmailData;

import com.easyelectroshop.amazonsesservice.Model.Customer.Address;

import java.util.List;

public record EmailData(
        String customerName,
        long orderId,
        List<OrderDetail> orderDetail,
        double totalContentPrice,
        double shippingCost,
        double totalOrderPrice,

        Address address

) {
}
