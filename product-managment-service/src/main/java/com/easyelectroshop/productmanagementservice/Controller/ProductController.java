package com.easyelectroshop.productmanagementservice.Controller;


import com.easyelectroshop.productmanagementservice.Model.Product;
import com.easyelectroshop.productmanagementservice.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/product-management")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/add-product")
    public void saveProduct(@RequestBody Product product){
        productService.saveProduct(product);
    }

    @GetMapping("get-product/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID productId){
        Optional<Product> product = productService.getProductById(productId);
        return(product.isPresent()) ? ResponseEntity.ok(product.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Product>> getAllProducts(){
        return new ResponseEntity<>(productService.getAllProducts(),HttpStatus.OK);
    }

    @PutMapping("/update-product")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product){
        Optional<Product> tempProduct = productService.getProductById(product.getProductId());
        if (tempProduct.isPresent()){
            return new ResponseEntity<>(productService.updateProduct(product).get(),HttpStatus.OK);
        } else {
            productService.saveProduct(product);
            return new ResponseEntity<>(productService.getProductById(product.getProductId()).get(),HttpStatus.CREATED);
        }
    }

}
