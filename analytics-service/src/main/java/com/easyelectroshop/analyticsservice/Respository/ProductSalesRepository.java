package com.easyelectroshop.analyticsservice.Respository;

import com.easyelectroshop.analyticsservice.Model.ProductSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductSalesRepository extends JpaRepository<ProductSales,Long> {

    public ProductSales findByProductId(UUID productId);

}
