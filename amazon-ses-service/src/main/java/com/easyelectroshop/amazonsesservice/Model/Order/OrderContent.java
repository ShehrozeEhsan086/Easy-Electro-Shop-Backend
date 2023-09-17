package com.easyelectroshop.amazonsesservice.Model.Order;

import java.util.UUID;

public record OrderContent(
        long orderContentId,
        UUID productId,
        int quantity
) {
}
