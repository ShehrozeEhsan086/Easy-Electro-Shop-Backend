package com.easyeletroshop.ratingservice.DTO.OrderDTO;

import java.util.UUID;

public record OrderContent(
        long orderContentId,
        UUID productId,
        int quantity
) {
}
