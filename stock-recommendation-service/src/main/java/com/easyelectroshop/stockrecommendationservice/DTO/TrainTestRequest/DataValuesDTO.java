package com.easyelectroshop.stockrecommendationservice.DTO.TrainTestRequest;

import java.util.UUID;

public record DataValuesDTO(
        String month,

        int year,

        int sales,
        UUID productId
) {
}
