package com.easyelectroshop.productmanagementservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "imageId")
    private UUID imageId;

    @Lob
    @Column(name = "imageData", columnDefinition="MEDIUMBLOB")
    private String imageData;

    private long colors;
}