package com.easyeletroshop.ratingservice.Repository;

import com.easyeletroshop.ratingservice.Model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RatingServiceRepository extends JpaRepository<Rating,Long> {

    @Query(value = "SELECT * FROM rating WHERE customer_id = ?1 AND order_id = ?2 LIMIT 1",nativeQuery = true)
    Rating findByCustomerIdAndOrderIdLimitOne(UUID customerId, long orderId);

    Rating findByCustomerIdAndProductId(UUID customerId, UUID productId);

}
