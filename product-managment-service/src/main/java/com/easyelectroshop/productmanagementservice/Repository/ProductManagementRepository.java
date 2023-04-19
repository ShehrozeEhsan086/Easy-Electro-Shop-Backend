package com.easyelectroshop.productmanagementservice.Repository;

import com.easyelectroshop.productmanagementservice.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductManagementRepository extends JpaRepository<Product, UUID> {



}