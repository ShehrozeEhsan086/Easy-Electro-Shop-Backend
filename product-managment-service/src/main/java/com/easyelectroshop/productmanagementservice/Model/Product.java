package com.easyelectroshop.productmanagementservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
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

    private boolean isDiscounted;

    private double discountPercentage;

    private double discountedPrice;

    private int quantity;

    private double size;

    @OneToMany(targetEntity = Colors.class,  cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_product_id",referencedColumnName = "productId")
    private List<Colors> colors;

    private long category;

    @OneToMany(targetEntity = SubCategory.class,  cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_product_id",referencedColumnName = "productId")
    private List<SubCategory> subCategories;

    private String _3DModel;

    private boolean available;

    private String lastUpdated;

}