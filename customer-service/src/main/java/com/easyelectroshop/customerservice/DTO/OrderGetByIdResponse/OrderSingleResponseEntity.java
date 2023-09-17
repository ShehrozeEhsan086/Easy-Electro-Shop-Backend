package com.easyelectroshop.customerservice.DTO.OrderGetByIdResponse;


import java.util.List;

public record OrderSingleResponseEntity(
        long orderId,
        List<OrderDetailResponse> orderDetail,
        double totalContentPrice,
        double shippingCost,
        double totalOrderPrice,
        String orderStatus,
        CustomerResponse customer

) {
}
