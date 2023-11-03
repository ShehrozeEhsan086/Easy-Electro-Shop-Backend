package com.easyelectroshop.analyticsservice.Respository;

import com.easyelectroshop.analyticsservice.Model.CitySales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitySalesRepository extends JpaRepository<CitySales,Long> {

    CitySales findByCity(String city);

    @Query(value = "SELECT * FROM city_sales ORDER BY sales_count LIMIT 5",nativeQuery = true)
    List<CitySales> findTopCitySales();

}