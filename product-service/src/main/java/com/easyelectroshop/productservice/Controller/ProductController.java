package com.easyelectroshop.productservice.Controller;

import com.easyelectroshop.productservice.DTO.Model3D;
import com.easyelectroshop.productservice.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

}
