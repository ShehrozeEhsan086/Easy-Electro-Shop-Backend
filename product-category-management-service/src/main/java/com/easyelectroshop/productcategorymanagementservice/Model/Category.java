package com.easyelectroshop.productcategorymanagementservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "categoryId")
    private long categoryId;

    private String categoryName;

    @OneToMany(targetEntity = SubCategory.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_category_id",referencedColumnName = "categoryId")
    private List<SubCategory> subCategories;


}
