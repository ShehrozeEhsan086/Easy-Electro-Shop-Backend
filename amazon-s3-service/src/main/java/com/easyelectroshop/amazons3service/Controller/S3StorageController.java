package com.easyelectroshop.amazons3service.Controller;

import com.easyelectroshop.amazons3service.DTO.Model3D;
import com.easyelectroshop.amazons3service.Service.S3StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatusCode;
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
        Model3D uploadedContent = s3StorageService.uploadModel(file);
        return(uploadedContent != null) ? ResponseEntity.ok(uploadedContent) : ResponseEntity.internalServerError().build();
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadModel(@PathVariable String fileName){
        byte[] data = s3StorageService.downloadModel(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type","application/octet-stream")
                .header("Content-disposition","attachment; filename=\""+fileName+"\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<HttpStatusCode> deleteModel(@PathVariable String fileName){
        return ResponseEntity.status(s3StorageService.deleteFile(fileName)).build();
    }
}