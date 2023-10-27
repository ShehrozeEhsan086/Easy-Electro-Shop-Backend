package com.easyelectroshop.productmanagementservice.DTO.ProductPriceQuantity;

public record ProductPriceQuantity(double price,
                                   int quantity) {
    public ProductPriceQuantity {
        // Constructor to map query results to the record
    }
}
