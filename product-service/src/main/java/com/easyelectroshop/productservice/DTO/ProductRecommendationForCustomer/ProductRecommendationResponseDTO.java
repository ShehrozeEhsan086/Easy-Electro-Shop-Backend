package com.easyelectroshop.productservice.DTO.ProductRecommendationForCustomer;

import java.util.List;

public record ProductRecommendationResponseDTO (
    List<String> recommendations
){
}