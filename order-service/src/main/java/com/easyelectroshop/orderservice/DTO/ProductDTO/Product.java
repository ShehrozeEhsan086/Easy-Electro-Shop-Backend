package com.easyelectroshop.orderservice.DTO.ProductDTO;


import java.util.List;
import java.util.UUID;

public record Product(UUID productId,
                      String name,
                      List<ProductImage> images,
                      String shortDescription,
                      String completeDescription,
                      String coverImage,
                      String brandName,
                      double price,
                      boolean isDiscounted,
                      double discountPercentage,
                      double discountedPrice,
                      int quantity,
                      String size,
                      List<Long> colors,
                      long category,
                      List<SubCategoryProduct> subCategories,
                      String _3DModelFilename,
                      String _3DModelURL,
                      boolean available,
                      String lastUpdated
                      ) {
}