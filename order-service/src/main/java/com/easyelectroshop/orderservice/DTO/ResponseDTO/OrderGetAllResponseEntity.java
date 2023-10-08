package com.easyelectroshop.orderservice.DTO.ResponseDTO;
import java.time.LocalDate;
import java.util.*;
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
