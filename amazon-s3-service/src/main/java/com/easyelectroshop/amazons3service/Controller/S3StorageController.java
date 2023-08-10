package com.easyelectroshop.amazons3service.Controller;

import com.easyelectroshop.amazons3service.DTO.Model3D;
import com.easyelectroshop.amazons3service.Service.S3StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/model")
public class S3StorageController {

    @Autowired
    S3StorageService s3StorageService;

    @PostMapping("/upload")
    public ResponseEntity<Model3D> uploadModel(@RequestParam(value = "model")MultipartFile file){
        return s3StorageService.uploadModel(file);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteModel(@PathVariable String fileName){
        return ResponseEntity.status(s3StorageService.deleteFile(fileName)).build();
    }
}