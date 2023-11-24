package com.easyelectroshop.stockrecommendationservice.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class DataValues {

    @Id
    @Column(name = "dataValueId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long dataValueId;

    private String month;

    private int year;

    private int sales;

}