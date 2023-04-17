package com.easyelectroshop.productservice.DTO.ProductDTO;

import java.util.List;
import java.util.UUID;

public record Product(UUID productId,
                      String name,
                      List<ProductImage> images,
                      String shortDescription,
                      String completeDescription,
                      String brandName,
                      String model,
                      double price,
                      boolean isDiscounted,
                      double discountPercentage,
                      double discountedPrice,
                      int quantity,
                      double size,
                      List<Colors> colors,
                      long category,
                      List<SubCategory> subCategories,
                      String _3DModel,
                      boolean available,
                      String lastUpdated
                      ) {
}
