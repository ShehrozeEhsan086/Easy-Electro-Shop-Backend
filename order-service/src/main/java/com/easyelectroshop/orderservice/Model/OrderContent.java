package com.easyelectroshop.orderservice.Model;

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
public class OrderContent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "orderContentId")
    private long orderContentId;

    private UUID productId;

    private int quantity;

}