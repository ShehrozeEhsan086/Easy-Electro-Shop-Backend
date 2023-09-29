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

    @Lob
    @Column(name = "completeDescription", columnDefinition="MEDIUMBLOB")
    private String completeDescription;

    @Lob
    @Column(name = "coverImage", columnDefinition="MEDIUMBLOB")
    private String coverImage;

    private String brandName;

    private double price;

    private int quantity;

    private String size;

    @ElementCollection
    private List<Long> colors;

    private long category;

    @OneToMany(targetEntity = SubCategory.class,  cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_product_id",referencedColumnName = "productId")
    private List<SubCategory> subCategories;

    private String _3DModelFilename;

    private String _3DModelURL;

    private boolean available;

    private String lastUpdated;


}