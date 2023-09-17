package com.easyelectroshop.orderservice.DTO.ProductDTO;

import java.util.UUID;

public record ProductImage(UUID imageId, String imageData, long colors) {
}
