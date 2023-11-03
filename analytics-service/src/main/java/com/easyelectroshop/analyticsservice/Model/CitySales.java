package com.easyelectroshop.analyticsservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CitySales {

    @Id
    @Column(name = "citySalesId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long citySalesId;

    private String city;

    private long salesCount;

}