package com.easyelectroshop.discountservice.Repository;

import com.easyelectroshop.discountservice.Model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiscountRepository extends JpaRepository<Discount,Long>{

    @Query(value = "SELECT * FROM discount where product_id = ?1 and is_active = true;",nativeQuery = true)
    Optional<Discount> getActiveByProductId(UUID productId);

    @Query(value = "SELECT * FROM discount where is_active = true;",nativeQuery = true)
    List<Discount> getAllActiveDiscounts();

    @Query(value = "SELECT * FROM discount where starts_at >= CURDATE();",nativeQuery = true)
    List<Discount> getAllFutureNonActiveDiscounts();

    List<Discount> findAllByProductId(UUID productId);

}