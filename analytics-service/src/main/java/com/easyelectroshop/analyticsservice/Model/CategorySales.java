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
public class CategorySales {

    @Id
    @Column(name = "categorySalesId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long categorySalesId;

    private long categoryId;

    private long salesCount;

}
