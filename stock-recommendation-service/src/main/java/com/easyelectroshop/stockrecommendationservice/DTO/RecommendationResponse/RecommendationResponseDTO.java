package com.easyelectroshop.stockrecommendationservice.DTO.RecommendationResponse;

import com.easyelectroshop.stockrecommendationservice.DTO.TrainTestRequest.DataEntityDTO;

public record RecommendationResponseDTO(
        Object forecastValues,
        DataEntityDTO actualValues
) {
}
