package com.easyelectroshop.analyticsservice.DTO.ProductCategory;

public record CategoryResponse(
        String categoryName,
        long totalSales
) {
}
