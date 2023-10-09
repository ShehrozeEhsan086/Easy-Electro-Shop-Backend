package com.easyelectroshop.orderservice.Respository;

import com.easyelectroshop.orderservice.Model.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long> {

    @Query(value = "SELECT * FROM order_entity ORDER BY ?1 ASC LIMIT ?2 OFFSET ?3",nativeQuery = true)
    List<OrderEntity> findAllPaginated(String sortBy, int pageSize, int pageNumber);

    @Query(value = "SELECT * FROM order_entity WHERE customer_id = ?1 ORDER BY ?2 ASC LIMIT ?3 OFFSET ?4",nativeQuery = true)
    List<OrderEntity> findAllPaginatedByCustomerId(UUID customerId, String sortBy, int pageSize, int pageNumber);

    List<OrderEntity> findAllByOrderStatus(String orderStatus);

    List<OrderEntity> findAllByCreatedAt(LocalDate createdAt);

    Page<OrderEntity> findAllByCustomerId(Pageable pageable, UUID customerId);

    Long countByOrderStatus(String orderStatus);

    Long countByCustomerId(UUID customerId);

    @Query(value = "SELECT * FROM order_entity_seq",nativeQuery = true)
    Long getOrderSequence();

}