package com.easyelectroshop.productservice.Service;

import com.easyelectroshop.productservice.DTO.Model3D;
import com.easyelectroshop.productservice.DTO.ProductDTO.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    public HttpStatusCode saveProduct(Product product) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO ADD PRODUCT"+product.toString());
        return webClientBuilder.build()
                .post()
                .uri("http://product-management-service/api/v1/product-management/add-product")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(product))
                .retrieve()
                .toBodilessEntity()
                .flatMap(response -> Mono.just(response.getStatusCode()))
                .block();
    }

    public List<Product> getAllProducts(int pageNumber,int pageSize,String sortBy) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET ALL PRODUCTS");
        try{
            ResponseEntity<List<Product>> products = webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-all?pageNumber="+pageNumber+"&pageSize="+pageSize+"&sort="+sortBy)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(Product.class)
                    .block();
            log.info("CALL SUCCESSFUL");
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

    public HttpStatusCode updateProduct(Product product) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO UPDATE PRODUCT WITH PRODUCT_ID "+product.productId());
        Product tempProduct = getProductById(product.productId());
        if (tempProduct != null){
            return webClientBuilder.build()
                    .put()
                    .uri("http://product-management-service/api/v1/product-management/update-product")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(product))
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } else {
            return saveProduct(product);
        }
    }


    public HttpStatusCode deleteProduct(UUID productId) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO DELETE PRODUCT WITH PRODUCT_ID "+productId);
        Product tempProduct = getProductById(productId);
        if (tempProduct != null){
            return webClientBuilder.build()
                    .delete()
                    .uri("http://product-management-service/api/v1/product-management/delete-product/"+productId)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } else {
            return HttpStatusCode.valueOf(404);
        }
    }
}