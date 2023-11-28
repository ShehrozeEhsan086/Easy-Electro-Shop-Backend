package com.easyeletroshop.ratingservice.Model;

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
public class Rating {

    @Id
    @Column(name = "ratingId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long ratingId;

    @Column(name = "customerId", nullable = false)
    private UUID customerId;

    @Column(name = "orderId", nullable = false)
    private long orderId;

    @Column(name = "productId", nullable = false)
    private UUID productId;

    @Column(name = "ratingValue", nullable = false)
    private int ratingValue;
}
