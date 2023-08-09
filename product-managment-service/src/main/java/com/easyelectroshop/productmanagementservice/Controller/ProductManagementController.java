package com.easyelectroshop.productmanagementservice.Controller;

import com.easyelectroshop.productmanagementservice.Model.Product;
import com.easyelectroshop.productmanagementservice.Service.ProductManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/product-management")
public class ProductManagementController {

    @Autowired
    ProductManagementService productManagementService;

    @PostMapping("/add-product")
    public ResponseEntity<HttpStatusCode> saveProduct(@RequestBody Product product){
        return ResponseEntity.status(productManagementService.saveProduct(product)).build();
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
            @RequestParam(value="sort",defaultValue = "lastUpdated",required = false) String sortBy)
    {
        int length = productManagementService.getProductsCount();
        if(length == 0){
            log.info("NO PRODUCTS FOUND IN DATABASE");
            return ResponseEntity.ok(null);
        } else {
            List<Product> products = productManagementService.getAllProducts(pageNumber,pageSize,sortBy);
            return (products != null) ? ResponseEntity.ok(products) : ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/get-all-count")
    public ResponseEntity<Integer> getProductsCount(){
        int length = productManagementService.getProductsCount();
        return (length >= 0) ? ResponseEntity.ok(length) : ResponseEntity.internalServerError().build() ;
    }

    @PutMapping("/update-product")
    public ResponseEntity<HttpStatusCode> updateProduct(@RequestBody Product product){
        return ResponseEntity.status(productManagementService.updateProduct(product)).build();
    }

    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<HttpStatusCode> deleteProduct(@PathVariable UUID productId){
        return ResponseEntity.status(productManagementService.deleteProduct(productId)).build();
    }

}