package com.easyelectroshop.customerservice.DTO.OrderGetByIdResponse;

public record OrderDetailResponse(
        String coverImage,
        String productName,
        double price,
        int quantity,
        double totalPrice
) {

}
