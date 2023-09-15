package com.easyelectroshop.amazonsesservice.Model.ProductDTO;

import java.util.UUID;

public record ProductImage(UUID imageId, String imageData, long colors) {
}
