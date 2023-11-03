package com.easyelectroshop.analyticsservice.Respository;

import com.easyelectroshop.analyticsservice.Model.CategorySales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategorySalesRepository extends JpaRepository<CategorySales,Long> {

    CategorySales findByCategoryId(long categoryId);

    @Query(value = "SELECT * FROM category_sales ORDER BY sales_count LIMIT 5",nativeQuery = true)
    List<CategorySales> findTopCategorySales();

}
