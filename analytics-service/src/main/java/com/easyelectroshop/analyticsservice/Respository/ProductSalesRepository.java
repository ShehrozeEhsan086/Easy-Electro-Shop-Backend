package com.easyelectroshop.analyticsservice.Respository;

import com.easyelectroshop.analyticsservice.Model.ProductSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductSalesRepository extends JpaRepository<ProductSales,Long> {

    ProductSales findByProductId(UUID productId);

    @Query(value = "SELECT * FROM product_sales ORDER BY sales_count LIMIT 5",nativeQuery = true)
    List<ProductSales> findTopProductSales();

}
