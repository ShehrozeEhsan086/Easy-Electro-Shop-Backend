package com.easyelectroshop.stripepaymentservice.Controller;

import com.easyelectroshop.stripepaymentservice.DTO.CreatePayment;
import com.easyelectroshop.stripepaymentservice.DTO.CreatePaymentResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stripe-service")
public class PaymentController {

    @PostMapping("/create-payment-intent")
    public CreatePaymentResponse createPaymentIntent(@RequestBody CreatePayment createPayment) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                        .setAmount(1500L)
                        .setCurrency("pkr")
                        .build();
            // Create a PaymentIntent with the order amount and currency
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return new CreatePaymentResponse(paymentIntent.getClientSecret());
    }
}