package com.easyelectroshop.productservice.Service;

import com.easyelectroshop.productservice.DTO.BundleRecommendation.BundleRecommendationResponse;
import com.easyelectroshop.productservice.DTO.BundleRecommendation.ProductIdDTO;
import com.easyelectroshop.productservice.DTO.ProductDTO.CompleteProductResponse;
import com.easyelectroshop.productservice.DTO.ProductDTO.ProductResponse;
import com.netflix.servo.monitor.StepCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
public class BundleRecommendationService {

    @Autowired
    ProductService productService;

    public ResponseEntity<CompleteProductResponse> getRecommendedProduct(UUID productId){
        try{
            ProductIdDTO productIdDTO = new ProductIdDTO(productId);

            BundleRecommendationResponse responseObject = WebClient.builder()
                    .baseUrl("http://127.0.0.1:5000/recommend_bundles")
                    .build()
                    .post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(productIdDTO))
                    .retrieve()
                    .bodyToMono(BundleRecommendationResponse.class)
                    .block();

            for (int i=0; i< responseObject.recommended_bundles().size(); i++){
                if (!responseObject.recommended_bundles().get(i).equals("0")){
                    return ResponseEntity.ok(productService.getProductById( UUID.fromString(responseObject.recommended_bundles().get(0))));
                }
            }

            System.out.println(responseObject.recommended_bundles().get(0).toString());



            return null;
        } catch (Exception ex){
            return ResponseEntity.internalServerError().build();
        }
    }


}