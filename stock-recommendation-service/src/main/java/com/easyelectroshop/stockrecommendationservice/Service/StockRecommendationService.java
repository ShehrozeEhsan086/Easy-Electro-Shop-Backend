package com.easyelectroshop.stockrecommendationservice.Service;

import com.easyelectroshop.stockrecommendationservice.DTO.RecommendationResponse.RecommendationResponseDTO;
import com.easyelectroshop.stockrecommendationservice.DTO.TrainTestRequest.DataEntityDTO;
import com.easyelectroshop.stockrecommendationservice.DTO.TrainTestRequest.DataValuesDTO;
import com.easyelectroshop.stockrecommendationservice.Model.DataEntity;
import com.easyelectroshop.stockrecommendationservice.Model.DataValues;
import com.easyelectroshop.stockrecommendationservice.Repository.StockRecommendationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class StockRecommendationService {

    private final  WebClient.Builder webClientBuilder;

    private final StockRecommendationRepository stockRecommendationRepository;

    public StockRecommendationService(WebClient.Builder webClientBuilder, StockRecommendationRepository stockRecommendationRepository) {
        this.webClientBuilder = webClientBuilder;
        this.stockRecommendationRepository = stockRecommendationRepository;
    }

    public ResponseEntity<HttpStatusCode> saveData(@RequestBody DataEntity dataEntity){
        log.info("ADDING NEW STOCK DATA");
        try{
            stockRecommendationRepository.save(dataEntity);
            return ResponseEntity.ok().build();
        } catch (Exception ex){
            log.error("ERROR ADDING NEW DATA",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<DataEntity> getDataEntityByProductId(UUID productId){
        log.info("GETTING DATA FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            return ResponseEntity.ok(stockRecommendationRepository.findByProductId(productId));
        } catch (Exception ex){
            log.error("ERROR GETTING DATA FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<RecommendationResponseDTO> getRecommendation(UUID productId){
        log.info("CALLING MACHINE LEARNING MODULE TO GET STOCK RECOMMENDATION FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            DataEntity productDataFromDatabase = stockRecommendationRepository.findByProductId(productId);
            if (productDataFromDatabase.getData().isEmpty()){
                log.error("DATA NOT FOUND FOR PRODUCT WITH PRODUCT_ID "+productId);
                return ResponseEntity.notFound().build();
            } else {
                List<DataValuesDTO> dataValuesDTOS = new ArrayList<>();
                for(int i = 0;i<productDataFromDatabase.getData().size();i++){
                    DataValuesDTO dataValuesDTO = new DataValuesDTO(productDataFromDatabase.getData().get(i).getMonth(),
                            productDataFromDatabase.getData().get(i).getYear(),
                            productDataFromDatabase.getData().get(i).getSales(),
                            productId);
                    dataValuesDTOS.add(dataValuesDTO);
                }
                DataEntityDTO dataEntityDTO = new DataEntityDTO(dataValuesDTOS);

                Object forecastResponse = WebClient.builder()
                        .baseUrl("http://127.0.0.1:5000/train_and_forecast")
                        .build()
                        .post()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(dataEntityDTO))
                        .retrieve()
                        .bodyToMono(Object.class)
                        .block();

                List<DataValuesDTO> currentYearSavedValues = new ArrayList<>();
                LocalDate currentDate = LocalDate.now();
                for(int i=0;i<productDataFromDatabase.getData().size();i++){
                    if(productDataFromDatabase.getData().get(i).getYear() == currentDate.getYear()){
                        currentYearSavedValues.add(new DataValuesDTO(productDataFromDatabase.getData().get(i).getMonth(),
                                productDataFromDatabase.getData().get(i).getYear(),
                                productDataFromDatabase.getData().get(i).getSales(),
                                null));
                    }
                }

                DataEntityDTO dataEntityDTOResponse = new DataEntityDTO(currentYearSavedValues);

                RecommendationResponseDTO recommendationResponseDTO = new RecommendationResponseDTO(forecastResponse,dataEntityDTOResponse);

                return ResponseEntity.ok(recommendationResponseDTO);
            }
            // getData for db
        } catch (Exception ex){
            log.error("ERROR CALLING MACHINE LEARNING MODULE TO GET STOCK RECOMMENDATION FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }


}
