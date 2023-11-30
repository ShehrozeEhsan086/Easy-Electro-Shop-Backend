package com.easyelectroshop.orderservice.DTO.OrdersInExcelSheet;

import java.util.UUID;

public record ExcelSheetDTO(
        long orderId,
        UUID productId,
        int quantity
) {
}
