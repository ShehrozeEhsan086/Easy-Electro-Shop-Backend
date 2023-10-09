package com.easyelectroshop.productmanagementservice.Repository;

import com.easyelectroshop.productmanagementservice.Model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.UUID;

@Repository
public interface ProductManagementRepository extends JpaRepository<Product, UUID> {

    @Query(value = "SELECT * FROM product ORDER BY ?1 ASC LIMIT ?2 OFFSET ?3 ",nativeQuery = true)
    List<Product> findAllPaginated(String sortBy, int pageSize, int pageNumber);

    @Query(value = "SELECT * FROM product WHERE name LIKE '%' ?1 '%' LIMIT 5", nativeQuery = true)
    List<Product> findTopFiveByName(String productName);

    @Query(value = "SELECT * FROM product WHERE name LIKE '%' ?1 '%' ORDER BY ?2 ASC LIMIT ?3 OFFSET ?4 ", nativeQuery = true)
    List<Product> findByName(String productName,String sortBy,int pageSize, int pageNumber);

    @Query(value = "SELECT price FROM product WHERE product_id = ?1",nativeQuery = true)
    Double findPriceByProductId(UUID productId);

    @Query(value = "SELECT quantity FROM product WHERE product_id = ?1",nativeQuery = true)
    int findStockByProductId(UUID productId);

    Page<Product> findAllByCategory(long category, Pageable pageable);

}