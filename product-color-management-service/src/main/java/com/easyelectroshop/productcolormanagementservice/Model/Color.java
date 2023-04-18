package com.easyelectroshop.productcolormanagementservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "colorId")
    private long colorId;

    private String colorName;

}