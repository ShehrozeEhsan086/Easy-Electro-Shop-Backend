package com.easyelectroshop.cartservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    @Column(name = "cartId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long cartId;

    @Column(name = "customerId", nullable = false)
    private UUID customerId;


    @OneToMany(targetEntity = CartContent.class,  cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_cart_id",referencedColumnName = "cartId")
    private List<CartContent> cartContent;

    private double totalPrice;
}