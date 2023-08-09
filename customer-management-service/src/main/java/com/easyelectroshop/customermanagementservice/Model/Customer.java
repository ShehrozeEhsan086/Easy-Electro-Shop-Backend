package com.easyelectroshop.customermanagementservice.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @Column(name = "customerId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID customerId;

    @Column(unique = true)
    private String userName;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String gender;

    @OneToOne(targetEntity = Address.class,  cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id",referencedColumnName = "addressId")
    private Address address;

    @JsonIgnore
    @OneToOne(targetEntity = PaymentMethod.class,  cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_option_id",referencedColumnName = "paymentOptionId")
    private PaymentMethod paymentOption;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isProfileComplete;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isBlocked;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private boolean isActive;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int totalOrders;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int totalOrdersAmount;


}