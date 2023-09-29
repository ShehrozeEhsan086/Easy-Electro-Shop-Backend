package com.easyelectroshop.discountservice.Controller;

import com.easyelectroshop.discountservice.Model.Discount;
import com.easyelectroshop.discountservice.Service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/discount")
public class DiscountController {

    @Autowired
    DiscountService discountService;

    @PostMapping("/add-new-discount")
    public ResponseEntity<HttpStatusCode> addDiscount(@RequestBody Discount discount){
        return discountService.addDiscount(discount);
    }

    @PutMapping("/edit-discount")
    public ResponseEntity<HttpStatusCode> editDiscount(@RequestBody Discount discount){
        return discountService.editDiscount(discount);
    }

    @PutMapping("/activate-discount/{discountId}")
    public ResponseEntity<HttpStatusCode> activateDiscount(@PathVariable long discountId){
        return discountService.activateDiscount(discountId);
    }

    @PutMapping("/deactivate-discount/{discountId}")
    public ResponseEntity<HttpStatusCode> deactivateDiscount(@PathVariable long discountId){
        return discountService.deactivateDiscount(discountId);
    }

    @GetMapping("/get-by-id/{discountId}")
    public ResponseEntity<Discount> getDiscountById(@PathVariable long discountId){
        return discountService.getById(discountId);
    }

    @GetMapping("/get-all-by-product-id/{productId}")
    public ResponseEntity<List<Discount>> getAllByProductId(@PathVariable UUID productId){
        return discountService.getAllByProductId(productId);
    }

    @GetMapping("/get-active-by-product-id/{productId}")
    public ResponseEntity<Discount> getActiveByProductId(@PathVariable UUID productId){
        return discountService.getActiveByProductId(productId);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Discount>> getAll(){
        return discountService.getAll();
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<HttpStatusCode> deleteAll(){
        return discountService.deleteAll();
    }

    @DeleteMapping("/delete-by-id/{discountId}")
    public ResponseEntity<HttpStatusCode> deleteById(@PathVariable long discountId){
        return discountService.deleteById(discountId);
    }

}