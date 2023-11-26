package com.easyeletroshop.ratingservice.Controller;

import com.easyeletroshop.ratingservice.DTO.RatedOrderDTO.RatedOrder;
import com.easyeletroshop.ratingservice.Service.RatingService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rating-service")
public class RatingServiceController {

    private final RatingService ratingService;

    public RatingServiceController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/add-order-rating")
    public ResponseEntity<HttpStatusCode> saveOrderRating(@RequestBody RatedOrder ratedOrder){
        return ratingService.addRatingToOrder(ratedOrder);
    }

    @GetMapping("/get-rating-by-orderid-customerid/{orderId}/{customerId}")
    public ResponseEntity<RatedOrder> getRatingByOrderIdAndCustomerId(@PathVariable long orderId,
                                                                      @PathVariable UUID customerId){
        return ratingService.getRatingByOrderIdAndCustomerId(orderId,customerId);
    }

}