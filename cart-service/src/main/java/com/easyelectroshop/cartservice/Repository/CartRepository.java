package com.easyelectroshop.cartservice.Repository;

import com.easyelectroshop.cartservice.Model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    public Optional<Cart> findByCustomerId(UUID customerId);

}