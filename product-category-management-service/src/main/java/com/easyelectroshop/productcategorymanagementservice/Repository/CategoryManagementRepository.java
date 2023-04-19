package com.easyelectroshop.productcategorymanagementservice.Repository;

import com.easyelectroshop.productcategorymanagementservice.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryManagementRepository extends JpaRepository<Category,Long> {


}