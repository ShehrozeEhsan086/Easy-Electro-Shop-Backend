package com.easyelectroshop.customermanagementservice.Model;

import com.easyelectroshop.customermanagementservice.Config.Encrypt;
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
public class PaymentOption {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "paymentOptionId")
    private long paymentOptionId;

    @Convert(converter = Encrypt.class)
    private String cardHolderName;

    @Convert(converter = Encrypt.class)
    private String cardNumber;

    @Convert(converter = Encrypt.class)
    private String expiryDate;

    @Convert(converter = Encrypt.class)
    private String cvv;

}