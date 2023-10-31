package com.easyelectroshop.analyticsservice.DTO.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OrderEntity(
        long orderId,

        UUID customerId,

        List<OrderContent> orderContent,

        double totalContentPrice,

        double shippingCost,

        double totalOrderPrice,

        String orderStatus,

        String shippingTrackingNumber,

        LocalDate createdAt,

        boolean onlinePayment
) {
}
