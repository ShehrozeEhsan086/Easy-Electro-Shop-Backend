package com.easyelectroshop.productservice.Controller;

import com.easyelectroshop.productservice.DTO.Model3D;
import com.easyelectroshop.productservice.DTO.ProductDTO.Product;
import com.easyelectroshop.productservice.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
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
    public ResponseEntity saveProduct(@RequestBody Product product){
        HttpStatusCode statusCode = productService.saveProduct(product);
        return ResponseEntity.status(statusCode).build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
                                                        @RequestParam(value="pageSize",defaultValue = "5",required = false) int pageSize,
                                                        @RequestParam(value="sort",defaultValue = "name",required = false) String sortBy){
        List<Product> products = productService.getAllProducts(pageNumber,pageSize,sortBy);
        return(products!=null) ? ResponseEntity.ok(products) : ResponseEntity.unprocessableEntity().build() ;
    }

    @GetMapping("get-product/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID productId){
        Product product = productService.getProductById(productId);
        return(product!=null) ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @PutMapping("/update-product")
    public ResponseEntity<Object> updateProduct(@RequestBody Product product){
        HttpStatusCode statusCode = productService.updateProduct(product);
        return ResponseEntity.status(statusCode).build();
    }

    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity deleteProduct(@PathVariable UUID productId){
        HttpStatusCode statusCode = productService.deleteProduct(productId);
        return ResponseEntity.status(statusCode).build();
    }


}