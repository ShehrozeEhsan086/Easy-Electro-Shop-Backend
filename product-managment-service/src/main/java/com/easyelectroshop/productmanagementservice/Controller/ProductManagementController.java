package com.easyelectroshop.productmanagementservice.Controller;


import com.easyelectroshop.productmanagementservice.Model.Product;
import com.easyelectroshop.productmanagementservice.Service.ProductManagementService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/product-management")
public class ProductManagementController {

    @Autowired
    ProductManagementService productManagementService;

    @PostMapping("/add-product")
    public ResponseEntity saveProduct(@RequestBody Product product){
        return (productManagementService.saveProduct(product)) ? ResponseEntity.ok().build() : ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("get-product/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID productId){
        Optional<Product> product = productManagementService.getProductById(productId);
        return(product.isPresent()) ? ResponseEntity.ok(product.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value="pageSize",defaultValue = "5",required = false) int pageSize,
            @RequestParam(value="sort",defaultValue = "productId",required = false) String sortBy)
    {
        List<Product> products = productManagementService.getAllProducts(pageNumber,pageSize,sortBy);
        return (products != null) ? ResponseEntity.ok(products) : ResponseEntity.unprocessableEntity().build();
    }

    @PutMapping("/update-product")
    public ResponseEntity updateProduct(@RequestBody Product product){
        return (productManagementService.updateProduct(product)) ? ResponseEntity.ok().build() : ResponseEntity.unprocessableEntity().build();
    }

    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity deleteProduct(@PathVariable UUID productId){
        return (productManagementService.deleteProduct(productId)) ? ResponseEntity.ok().build() : ResponseEntity.unprocessableEntity().build();
    }

}