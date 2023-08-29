package com.easyelectroshop.customerservice.DTO.Order;

import java.time.LocalDate;
import java.util.UUID;

import java.util.*;
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
