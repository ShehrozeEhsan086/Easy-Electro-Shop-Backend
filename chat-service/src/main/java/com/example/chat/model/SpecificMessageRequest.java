package com.example.chat.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecificMessageRequest {

    String senderName ;
    String receiverName;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}
