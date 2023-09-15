package com.easyelectroshop.amazonsesservice.Model.EmailData;

import com.easyelectroshop.amazonsesservice.Model.Customer.Address;

import java.util.List;

public record EmailData(
        String customerName,
        long orderId,
        List<OrderDetail> orderDetail,
        double totalContentPrice,
        double shippingCost,
        double totalOrderPrice,
        String orderStatus,
        Address address

) {

    @Override
    public String toString() {
        return "Easy Electro Shop \nDear "+customerName+", we have received your order. Hold tight while our staff works day and night to complete your order. ;)" +
                "\nOrder Number: "+orderId+"\nOrder Status: "+orderStatus+"\nOrder Summary:\n "+orderDetail+"\t\nSubtotal: Rs. "+totalContentPrice+"\t\nShipping Cost: Rs. "
                +shippingCost +"\t\nOrder Total: "+totalOrderPrice+"\n\nDelivery Details:\n"+address;
    }
}
