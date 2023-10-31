package com.easyelectroshop.analyticsservice.Respository;

import com.easyelectroshop.analyticsservice.Model.CategorySales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorySalesRepository extends JpaRepository<CategorySales,Long> {

    CategorySales findByCategoryId(long categoryId);

}
