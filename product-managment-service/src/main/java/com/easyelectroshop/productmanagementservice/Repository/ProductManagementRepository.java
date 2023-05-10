package com.easyelectroshop.productmanagementservice.Repository;

import com.easyelectroshop.productmanagementservice.Model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductManagementRepository extends JpaRepository<Product, UUID> {

    @Query(value = "SELECT * FROM product_management_service_database.product p LEFT JOIN product_management_service_database.product_image pi ON p.product_id = pi.fk_product_id",nativeQuery = true)
    Page<Product> findAllWithOneImage(Pageable pageable);


}