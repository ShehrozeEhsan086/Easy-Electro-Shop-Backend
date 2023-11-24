package com.easyelectroshop.stockrecommendationservice.DTO.TrainTestRequest;

import com.easyelectroshop.stockrecommendationservice.Model.DataValues;

import java.util.List;

public record DataEntityDTO(
        List<DataValuesDTO> data
) {
}
