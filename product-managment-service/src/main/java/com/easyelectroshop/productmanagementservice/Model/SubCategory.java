package com.easyelectroshop.productmanagementservice.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubCategory {

    @Id
    @Column(name = "subCategoryId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long subCategoryId;

    private long subCategoryFkId;

}
