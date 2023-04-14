package com.easyelectroshop.productservice.Service;

import com.easyelectroshop.productservice.DTO.Model3D;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Service
@Slf4j
public class ProductService {

    @Autowired
    WebClient.Builder webClientBuilder;

    @Autowired
    MultipartBodyBuilder multipartBodyBuilder;


    public Model3D uploadModel(MultipartFile file) {
      log.info("Calling Amazon S3 Service to upload "+file.getOriginalFilename());
      multipartBodyBuilder.part("model",file.getResource());
      var payLoad = multipartBodyBuilder.build();
      return webClientBuilder.build()
              .post()
              .uri("http://amazon-s3-service/api/v1/model/upload")
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .body(BodyInserters.fromMultipartData(payLoad))
              .retrieve()
              .bodyToMono(Model3D.class)
              .block();
    }
}