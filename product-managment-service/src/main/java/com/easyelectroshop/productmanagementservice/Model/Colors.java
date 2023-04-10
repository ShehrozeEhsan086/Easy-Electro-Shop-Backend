package com.easyelectroshop.productmanagementservice.Model;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Colors {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="colorId")
    private long colorId;

    private String colorName;

}