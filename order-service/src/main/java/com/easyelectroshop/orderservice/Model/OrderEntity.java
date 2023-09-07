package com.easyelectroshop.orderservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

    @Id
    @Column(name = "orderId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long orderId;

    @Column(name = "customerId", nullable = false)
    private UUID customerId;

    @OneToMany(targetEntity = OrderContent.class,  cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_order_id",referencedColumnName = "orderId")
    private List<OrderContent> orderContent;

    private double totalContentPrice;

    private double shippingCost;

    private double totalOrderPrice;

    private String orderStatus;

    private String shippingTrackingNumber;

    private LocalDate createdAt;

    private boolean onlinePayment;

}