package com.easyelectroshop.analyticsservice.Model;

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
public class ProductSales {

    @Id
    @Column(name = "productSalesId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long productSalesId;

    private UUID productId;

    private long salesCount;
}