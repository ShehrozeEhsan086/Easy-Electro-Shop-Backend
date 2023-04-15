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
    private long subCategoryId;

}
