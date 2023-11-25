package com.easyelectroshop.stockrecommendationservice.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataEntity {

    @Id
    @Column(name = "dataEntityID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long dataEntityID;

    private UUID productId;

    @OneToMany(targetEntity = DataValues.class,  cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_dataentity_id",referencedColumnName = "dataEntityID")
    private List<DataValues> data;

}