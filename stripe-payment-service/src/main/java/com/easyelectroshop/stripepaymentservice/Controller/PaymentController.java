package com.easyelectroshop.stripepaymentservice.Controller;

import com.easyelectroshop.stripepaymentservice.DTO.CreatePayment;
import com.easyelectroshop.stripepaymentservice.DTO.CreatePaymentResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/stripe-service")
public class PaymentController {

    @PostMapping("/create-payment-intent")
    public CreatePaymentResponse createPaymentIntent(@RequestBody CreatePayment createPayment) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                        .setAmount(createPayment.getTotalPrice())
                        .setCurrency("pkr")
                        .build();
            // Create a PaymentIntent with the order amount and currency
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return new CreatePaymentResponse(paymentIntent.getClientSecret());
    }
}