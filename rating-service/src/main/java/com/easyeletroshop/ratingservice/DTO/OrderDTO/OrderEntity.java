package com.easyeletroshop.ratingservice.DTO.OrderDTO;

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
        String createdAt,
        boolean onlinePayment
) {
}
