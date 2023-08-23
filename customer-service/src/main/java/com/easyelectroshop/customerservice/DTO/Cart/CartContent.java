package com.easyelectroshop.customerservice.DTO.Cart;

import java.util.UUID;

public record CartContent(
        long cartContentId,
        UUID productId,
        int quantity
) {
}
