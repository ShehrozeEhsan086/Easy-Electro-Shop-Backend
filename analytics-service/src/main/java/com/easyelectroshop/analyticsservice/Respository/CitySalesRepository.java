package com.easyelectroshop.analyticsservice.Respository;

import com.easyelectroshop.analyticsservice.Model.CitySales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitySalesRepository extends JpaRepository<CitySales,Long> {

    CitySales findByCity(String city);

}