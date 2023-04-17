package com.easyelectroshop.productservice.Service;

import com.easyelectroshop.productservice.DTO.Model3D;
import com.easyelectroshop.productservice.DTO.ProductDTO.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

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

    public void saveProduct(Product product) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO ADD PRODUCT"+product.toString());
        webClientBuilder.build()
                .post()
                .uri("http://product-management-service/api/v1/product-management/add-product")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(product))
                .retrieve()
                .bodyToMono(Product.class)
                .block();
    }

    public List<Product> getAllProducts() {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET ALL PRODUCTS");
        try{
            ResponseEntity<List<Product>> products = webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-all")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(Product.class)
                    .block();
            return products.getBody();
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE ALL PRODUCTS",ex);
            return null;
        }
    }

    public Product getProductById(UUID productId) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT WITH PRODUCT_ID "+productId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-product/"+productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .block();
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE PRODUCT WITH PRODUCT_ID "+productId,ex);
            return null;
        }
    }

    public void updateProduct(Product product) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO UPDATE PRODUCT WITH PRODUCT_ID "+product.productId());
        webClientBuilder.build()
                .post()
                .uri("http://product-management-service/api/v1/product-management/add-product")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(product))
                .retrieve()
                .bodyToMono(Product.class)
                .block();
    }
}