package com.easyelectroshop.analyticsservice.DTO.Province;

public record ProvinceResponse(
        String provinceName,
        long salesCount,
        double salesPercentage
) {
}
