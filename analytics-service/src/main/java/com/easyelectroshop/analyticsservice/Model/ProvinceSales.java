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
public class ProvinceSales {

    @Id
    @Column(name = "provinceSalesId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long provinceSalesId;

    private String province;

    private long salesCount;

}