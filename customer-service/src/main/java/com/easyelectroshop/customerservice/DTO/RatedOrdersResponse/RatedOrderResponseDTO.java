package com.easyelectroshop.customerservice.DTO.RatedOrdersResponse;

import com.easyelectroshop.customerservice.DTO.OrderGetAllResponse.OrderGetAllResponseEntity;

import java.time.LocalDate;
import java.util.List;

public record RatedOrderResponseDTO(
        OrderGetAllResponseEntity orderGetAllResponseEntity,
        int ratingValue
) {
}
