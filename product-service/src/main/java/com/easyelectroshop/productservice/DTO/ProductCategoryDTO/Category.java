package com.easyelectroshop.productservice.DTO.ProductCategoryDTO;
import java.util.List;

public record Category(long categoryId, String categoryName, List<SubCategory> subCategories) {
}
