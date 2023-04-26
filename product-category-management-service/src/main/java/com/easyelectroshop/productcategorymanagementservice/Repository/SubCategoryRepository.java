package com.easyelectroshop.productcategorymanagementservice.Repository;

import com.easyelectroshop.productcategorymanagementservice.Model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory,Long> {

    @Query(value = "SELECT sub_category_id,sub_category_name FROM sub_category where fk_category_id = ?1",nativeQuery = true)
    List<SubCategory> getSubCategoriesByCategoryId(long categoryId);


}
