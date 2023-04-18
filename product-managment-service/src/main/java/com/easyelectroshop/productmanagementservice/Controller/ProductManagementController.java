package com.easyelectroshop.productmanagementservice.Controller;


import com.easyelectroshop.productmanagementservice.Model.Product;
import com.easyelectroshop.productmanagementservice.Service.ProductManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/product-management")
@CrossOrigin
public class ProductManagementController {

    @Autowired
    ProductManagementService productManagementService;

    @PostMapping("/add-product")
    public void saveProduct(@RequestBody Product product){
        productManagementService.saveProduct(product);
    }

    @GetMapping("get-product/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID productId){
        Optional<Product> product = productManagementService.getProductById(productId);
        return(product.isPresent()) ? ResponseEntity.ok(product.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Product>> getAllProducts(){
        return new ResponseEntity<>(productManagementService.getAllProducts(),HttpStatus.OK);
    }

    @PutMapping("/update-product")
    public void updateProduct(@RequestBody Product product){
        Optional<Product> tempProduct = productManagementService.getProductById(product.getProductId());
        if (tempProduct.isPresent()){
            productManagementService.updateProduct(product);
        } else {
            productManagementService.saveProduct(product);
        }
    }
}
