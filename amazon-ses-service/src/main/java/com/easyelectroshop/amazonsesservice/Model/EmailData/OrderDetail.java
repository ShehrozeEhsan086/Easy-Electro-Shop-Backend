package com.easyelectroshop.amazonsesservice.Model.EmailData;

public record OrderDetail(
        String coverImage,
        String productName,
        double price,
        int quantity,
        double totalPrice
) {

    @Override
    public String toString() {
        return productName+" Rs. "+price+" x "+quantity+"\t Rs. "+totalPrice;
    }
}
