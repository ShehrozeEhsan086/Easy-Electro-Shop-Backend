package com.easyelectroshop.productmanagementservice.Model;

import jakarta.persistence.*;
import org.hibernate.engine.internal.Cascade;

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

    public Product() {
    }

    public Product(UUID productId, String name, List<ProductImage> images, String shortDescription, String completeDescription, String brandName, String model, double price) {
        this.productId = productId;
        this.name = name;
        this.images = images;
        this.shortDescription = shortDescription;
        this.completeDescription = completeDescription;
        this.brandName = brandName;
        this.model = model;
        this.price = price;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getCompleteDescription() {
        return completeDescription;
    }

    public void setCompleteDescription(String completeDescription) {
        this.completeDescription = completeDescription;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
