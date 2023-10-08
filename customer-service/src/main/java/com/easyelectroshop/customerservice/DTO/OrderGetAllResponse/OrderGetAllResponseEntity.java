package com.easyelectroshop.customerservice.DTO.OrderGetAllResponse;

import java.time.LocalDate;
import java.util.List;

public record OrderGetAllResponseEntity(
        long orderId,
        List<String> productNames,
        String customerName,
        Double totalPrice,
        LocalDate createdAt,
        String orderStatus,
        String shippingNumber
) {
}
