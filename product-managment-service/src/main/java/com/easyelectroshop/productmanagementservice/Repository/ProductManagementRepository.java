package com.easyelectroshop.productmanagementservice.Repository;

import com.easyelectroshop.productmanagementservice.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductManagementRepository extends JpaRepository<Product, UUID> {



}