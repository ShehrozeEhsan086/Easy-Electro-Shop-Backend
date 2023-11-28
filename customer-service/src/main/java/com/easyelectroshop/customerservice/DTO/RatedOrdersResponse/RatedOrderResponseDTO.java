package com.easyelectroshop.customerservice.DTO.RatedOrdersResponse;

import java.time.LocalDate;
import java.util.List;

public record RatedOrderResponseDTO(
        long orderId,
        List<String> productNames,
        String customerName,
        Double totalPrice,
        LocalDate createdAt,
        String orderStatus,
        String shippingNumber,
        int ratingValue
) {
}
