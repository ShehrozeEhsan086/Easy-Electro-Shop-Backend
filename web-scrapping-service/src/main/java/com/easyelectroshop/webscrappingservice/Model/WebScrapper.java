package com.easyelectroshop.webscrappingservice.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebScrapper {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long scrapperId;

    private UUID productId;

    private String site;

    private String scrappedPrice;

    private boolean isVisible;

}