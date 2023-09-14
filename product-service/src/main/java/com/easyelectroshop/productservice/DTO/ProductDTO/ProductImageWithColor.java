package com.easyelectroshop.productservice.DTO.ProductDTO;

import java.util.UUID;

public record ProductImageWithColor(UUID imageId, String imageData, String colors) {
}
