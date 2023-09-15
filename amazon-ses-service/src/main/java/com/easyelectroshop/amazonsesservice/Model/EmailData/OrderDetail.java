package com.easyelectroshop.amazonsesservice.Model.EmailData;

public record OrderDetail(
        String coverImage,
        String productName,
        double price,
        int quantity,
        double totalPrice
) {
}
