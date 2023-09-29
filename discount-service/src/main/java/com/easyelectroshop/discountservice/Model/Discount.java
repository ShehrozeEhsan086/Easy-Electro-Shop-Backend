package com.easyelectroshop.discountservice.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Discount {

    @Id
    @Column(name = "discountId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long discountId;

    @Column(name = "productId", nullable = false)
    private UUID productId;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate startsAt;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate endsAt;

    private int discountPercentage;

    private boolean isActive;

}