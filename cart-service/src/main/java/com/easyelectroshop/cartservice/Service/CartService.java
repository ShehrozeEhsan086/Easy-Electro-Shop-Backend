package com.easyelectroshop.cartservice.Service;

import com.easyelectroshop.cartservice.Repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class CartService {

    @Autowired
    CartRepository cartRepository;

    public int addToCart(UUID customerId, UUID productId, int quantity) {

    }
}