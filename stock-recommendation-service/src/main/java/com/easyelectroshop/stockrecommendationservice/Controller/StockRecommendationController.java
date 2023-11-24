package com.easyelectroshop.stockrecommendationservice.Controller;

import com.easyelectroshop.stockrecommendationservice.DTO.RecommendationResponse.RecommendationResponseDTO;
import com.easyelectroshop.stockrecommendationservice.Model.DataEntity;
import com.easyelectroshop.stockrecommendationservice.Service.StockRecommendationService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stock-recommendation-service")
public class StockRecommendationController {

    private final StockRecommendationService stockRecommendationService;

    public StockRecommendationController(StockRecommendationService stockRecommendationService) {
        this.stockRecommendationService = stockRecommendationService;
    }

    @PostMapping("/save-data")
    public ResponseEntity<HttpStatusCode> saveData(@RequestBody DataEntity dataEntity){
        return stockRecommendationService.saveData(dataEntity);
    }

    @GetMapping("/get-data/{productId}")
    public ResponseEntity<DataEntity> getDataByProductId(@PathVariable UUID productId){
        return stockRecommendationService.getDataEntityByProductId(productId);
    }

    @GetMapping("/get-recommendation/{productId}")
    public ResponseEntity<RecommendationResponseDTO> getRecommendationById(@PathVariable UUID productId){
        return stockRecommendationService.getRecommendation(productId);
    }

}