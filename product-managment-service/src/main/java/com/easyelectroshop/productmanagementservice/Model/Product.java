package com.easyelectroshop.productmanagementservice.Model;

import jakarta.persistence.*;


import java.sql.Timestamp;
import java.util.UUID;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "productId")
    private UUID productId;
    private String name;

    @OneToMany(targetEntity = ProductImage.class,  cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_product_id",referencedColumnName = "productId")
    private List<ProductImage> images;
    private String shortDescription;
    private String completeDescription;
    private String brandName;
    private String model;
    private double price;
    private int quantity;
    private int category;
    private String _3DModel;
    private boolean available;
    private Timestamp lastUpdated;



}
