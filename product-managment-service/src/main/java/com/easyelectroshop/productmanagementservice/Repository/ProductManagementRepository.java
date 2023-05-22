package com.easyelectroshop.productmanagementservice.Repository;

import com.easyelectroshop.productmanagementservice.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.UUID;

@Repository
public interface ProductManagementRepository extends JpaRepository<Product, UUID> {


    // FIX Later not working correctly!
    @Query(value = "SELECT * FROM product ORDER BY ?1 ASC LIMIT ?2 OFFSET ?3 ",nativeQuery = true)
    List<Product> findAllWithOnlyCoverImage(String sortBy,int pageSize, int pageNumber);
}