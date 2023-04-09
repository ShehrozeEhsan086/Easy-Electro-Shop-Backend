package com.easyelectroshop.productmanagementservice.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Colors {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String colorName;

//    imageid // 3 -> color Many TO many

}
