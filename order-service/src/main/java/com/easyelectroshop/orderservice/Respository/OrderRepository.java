package com.easyelectroshop.orderservice.Respository;

import com.easyelectroshop.orderservice.Model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long> {

    @Query(value = "SELECT * FROM order_entity ORDER BY ?1 ASC LIMIT ?2 OFFSET ?3",nativeQuery = true)
    List<OrderEntity> findAllPaginated(String sortBy, int pageSize, int pageNumber);

    List<OrderEntity> findAllByOrderStatus(String orderStatus);

    List<OrderEntity> findAllByCreatedAt(LocalDate createdAt);


}