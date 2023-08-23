package com.easyelectroshop.customerservice.DTO.Cart;

import java.util.*;

public record Cart(
        long cartId,
        UUID customerId,
        List<CartContent> cartContent,
        double totalPrice
) {
}
