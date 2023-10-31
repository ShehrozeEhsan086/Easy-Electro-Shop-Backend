package com.easyelectroshop.analyticsservice.DTO.ProductCategory;

import java.util.List;

public record Category(long categoryId, String categoryName, List<SubCategory> subCategories) {
}
