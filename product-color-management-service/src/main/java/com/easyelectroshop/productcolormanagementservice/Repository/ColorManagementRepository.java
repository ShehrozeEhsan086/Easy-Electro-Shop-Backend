package com.easyelectroshop.productcolormanagementservice.Repository;

import com.easyelectroshop.productcolormanagementservice.Model.Color;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorManagementRepository extends JpaRepository<Color, Long> {

}
