package com.easyelectroshop.productservice.Controller;

import com.easyelectroshop.productservice.DTO.Model3D;
import com.easyelectroshop.productservice.DTO.ProductDTO.Product;
import com.easyelectroshop.productservice.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("api/v1/product")
public class ProductController {

    @Autowired
    ProductService productService;


    @PostMapping("/upload-model")
    public ResponseEntity<Model3D> uploadModel(@RequestParam(value = "model") MultipartFile file){
        Model3D uploadedContent = productService.uploadModel(file);
        return(uploadedContent != null) ? ResponseEntity.ok(uploadedContent) : ResponseEntity.internalServerError().build();
    }

    @PostMapping("/add-product")
    public void saveProduct(@RequestBody Product product){
        productService.saveProduct(product);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Product>> getAllProducts(){
        List<Product> products = productService.getAllProducts();
        return(products!=null) ? ResponseEntity.ok(products) : ResponseEntity.notFound().build() ;
    }

    @GetMapping("get-product/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID productId){
        Product product = productService.getProductById(productId);
        return(product!=null) ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @PutMapping("/update-product")
    public void updateProduct(@RequestBody Product product){
        Product tempProduct = productService.getProductById(product.productId());
        if (tempProduct != null){
            productService.updateProduct(product);
        } else {
            productService.saveProduct(product);
        }
    }

}