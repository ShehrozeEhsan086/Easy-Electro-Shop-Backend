package com.easyelectroshop.analyticsservice.Respository;

import com.easyelectroshop.analyticsservice.Model.ProvinceSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceSalesRepository extends JpaRepository<ProvinceSales,Long> {

    ProvinceSales findByProvince(String province);

}
