package com.easyelectroshop.productcolormanagementservice.Repository;

import com.easyelectroshop.productcolormanagementservice.Model.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorManagementRepository extends JpaRepository<Color, Long> {

}
