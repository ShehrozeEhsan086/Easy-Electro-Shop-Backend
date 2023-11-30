package com.easyelectroshop.productservice.DTO.BundleRecommendation;

import java.util.List;

public record BundleRecommendationResponse(
        List<String> recommended_bundles
) {
}
