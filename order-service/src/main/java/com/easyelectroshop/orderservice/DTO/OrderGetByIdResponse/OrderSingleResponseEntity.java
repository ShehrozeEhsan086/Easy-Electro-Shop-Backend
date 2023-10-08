package com.easyelectroshop.orderservice.DTO.OrderGetByIdResponse;


import java.util.List;

public record OrderSingleResponseEntity(
        long orderId,
        List<OrderDetailResponse> orderDetail,
        double totalContentPrice,
        double shippingCost,
        double totalOrderPrice,
        String orderStatus,
        String shippingNumber,
        CustomerResponse customer

) {
}
