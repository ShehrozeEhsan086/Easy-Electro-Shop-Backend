package com.easyelectroshop.stripepaymentservice.DTO;

import com.google.gson.annotations.SerializedName;

public class CreatePayment {
    @SerializedName("items")
    Object[] items;

    long totalPrice;

    public long getTotalPrice() {
        return totalPrice;
    }

    public Object[]  getItems() {
        return items;
    }
}
