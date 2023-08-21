package com.easyelectroshop.cartservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartContent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cartContentId")
    private long cartContentId;

    private UUID productId;

    private int quantity;


}