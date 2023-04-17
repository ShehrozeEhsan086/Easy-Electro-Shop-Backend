package com.easyelectroshop.productservice.DTO.ProductDTO;

import java.util.UUID;

public record ProductImage(UUID imageId, String imageData, long colors) {
}
