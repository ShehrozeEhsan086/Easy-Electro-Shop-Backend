package com.easyelectroshop.discountservice.Repository;

import com.easyelectroshop.discountservice.Model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DiscountRepository extends JpaRepository<Discount,Long>{

    @Query(value = "SELECT * FROM discount where product_id = ?1 and is_active = true;",nativeQuery = true)
    Optional<Discount> getActiveByProductId(UUID productId);

    List<Discount> findAllByProductId(UUID productId);
}