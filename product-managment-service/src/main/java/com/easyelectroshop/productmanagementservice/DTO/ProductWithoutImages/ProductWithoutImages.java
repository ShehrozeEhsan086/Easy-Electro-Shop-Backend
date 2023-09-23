package com.easyelectroshop.productmanagementservice.DTO.ProductWithoutImages;


import com.easyelectroshop.productmanagementservice.Model.SubCategory;

import java.util.List;
import java.util.UUID;

public record ProductWithoutImages(UUID productId,
                                   String name,
                                   String shortDescription,
                                   String completeDescription,
                                   String coverImage,
                                   String brandName,
                                   double price,
                                   int quantity,
                                   String size,
                                   List<Long> colors,
                                   long category,
                                   List<SubCategory> subCategories,
                                   String _3DModelFilename,
                                   String _3DModelURL,
                                   boolean available,
                                   String lastUpdated
                      ) {
}