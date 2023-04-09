package com.easyelectroshop.productmanagementservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
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


//    @ManyToOne
//    @Column(name = "")
//    private List<Colors> colors;



}