package com.easyelectroshop.productmanagementservice.Model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "imageId")
    private UUID imageId;

    @Lob
    @Column(name = "imageData", columnDefinition="MEDIUMBLOB")
    private String imageData;


    public ProductImage() {
    }

    public ProductImage(UUID imageId, String imageData) {
        this.imageId = imageId;
        this.imageData = imageData;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
}