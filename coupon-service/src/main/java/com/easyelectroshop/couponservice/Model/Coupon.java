package com.easyelectroshop.couponservice.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @Column(name = "couponId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long couponId;

    private String couponCode;

    @Column(name = "customerId", nullable = false)
    private UUID customerId;

    private int discountPercentage;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate validUpto;

}