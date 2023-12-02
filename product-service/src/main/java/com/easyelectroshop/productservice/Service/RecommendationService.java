package com.easyelectroshop.productservice.Service;

import com.easyelectroshop.productservice.DTO.BundleRecommendation.BundleRecommendationResponse;
import com.easyelectroshop.productservice.DTO.BundleRecommendation.ProductIdDTO;
import com.easyelectroshop.productservice.DTO.ProductDTO.CompleteProductResponse;
import com.easyelectroshop.productservice.DTO.ProductRecommendationForCustomer.ProductRecommendationRequestDTO;
import com.easyelectroshop.productservice.DTO.ProductRecommendationForCustomer.ProductRecommendationResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RecommendationService {

    @Autowired
    ProductService productService;

    public ResponseEntity<CompleteProductResponse> getRelatedProductRecommendation(UUID productId){
        try{
            log.info("GETTING BUNDLE RECOMMENDATION FOR PRODUCT WITH PRODUCT_ID "+productId);
            ProductIdDTO productIdDTO = new ProductIdDTO(productId);

            BundleRecommendationResponse responseObject = WebClient.builder()
                    .baseUrl("http://127.0.0.1:5001/recommend_bundles")
                    .build()
                    .post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(productIdDTO))
                    .retrieve()
                    .bodyToMono(BundleRecommendationResponse.class)
                    .block();

            for (int i=0; i< responseObject.recommended_bundles().size(); i++){
                if (!responseObject.recommended_bundles().get(i).equals("0")){
                    return ResponseEntity.ok(productService.getProductById( UUID.fromString(responseObject.recommended_bundles().get(i))));
                }
            }
            log.warn("NO RECOMMENDATION RECIEVEVED");
            return ResponseEntity.noContent().build();
        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.noContent().build();
        }
    }

    public ResponseEntity<List<CompleteProductResponse>> getProductForCustomerRecommendation(UUID customerId){
        try{
            log.info("GETTING PRODUCT RECOMMENDATION FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
            ProductRecommendationRequestDTO productRecommendationRequestDTO = new ProductRecommendationRequestDTO(customerId,5);

            ProductRecommendationResponseDTO responseObject = WebClient.builder()
                    .baseUrl("http://127.0.0.1:5001/get_recommendations")
                    .build()
                    .post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(productRecommendationRequestDTO))
                    .retrieve()
                    .bodyToMono(ProductRecommendationResponseDTO.class)
                    .block();

            List<CompleteProductResponse> responses = new ArrayList<>();

            for (int i=0; i< responseObject.recommendations().size() ; i++){
                responses.add(productService.getProductById(UUID.fromString( responseObject.recommendations().get(i))));
            }
            return ResponseEntity.ok(responses);

        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.noContent().build();
        }
    }


}