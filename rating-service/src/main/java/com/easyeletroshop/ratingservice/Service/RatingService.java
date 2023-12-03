package com.easyeletroshop.ratingservice.Service;

import com.easyeletroshop.ratingservice.DTO.OrderDTO.OrderEntity;
import com.easyeletroshop.ratingservice.DTO.RatedOrderDTO.RatedOrder;
import com.easyeletroshop.ratingservice.Model.Rating;
import com.easyeletroshop.ratingservice.Repository.RatingServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RatingService {

    private final RatingServiceRepository ratingServiceRepository;

    private final WebClient.Builder webClientBuilder;


    public RatingService(RatingServiceRepository ratingServiceRepository, WebClient.Builder webClientBuilder) {
        this.ratingServiceRepository = ratingServiceRepository;
        this.webClientBuilder = webClientBuilder;
    }


    public ResponseEntity<HttpStatusCode> addRatingToOrder(RatedOrder ratedOrder){
        log.info("ADDING RATING FOR ORDER WITH ORDER_ID "+ratedOrder.orderId());
        try{
            OrderEntity orderObject = webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-order-by-id-original-object/"+ratedOrder.orderId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(OrderEntity.class)
                    .block()
                    .getBody();

            for(int i=0;i<orderObject.orderContent().size();i++){

                Rating existingRating = ratingServiceRepository.findByCustomerIdAndProductId(ratedOrder.customerId(),orderObject.orderContent().get(i).productId());
                if(existingRating == null){
                    Rating rating = new Rating();
                    rating.setRatingValue(ratedOrder.ratingValue());
                    rating.setOrderId(ratedOrder.orderId());
                    rating.setCustomerId(ratedOrder.customerId());
                    rating.setProductId(orderObject.orderContent().get(i).productId());
                    ratingServiceRepository.save(rating);
                } else {
                    continue;
                }
            }

            return ResponseEntity.ok().build();

        } catch (Exception ex){
            log.error("ERROR ADDING RATING FOR ORDER WITH ORDER_ID "+ratedOrder.orderId(),ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<RatedOrder> getRatingByOrderIdAndCustomerId(long orderId, UUID customerId) {
        log.info("GETTING RATING FOR ORDER WITH ORDER_ID "+orderId+" FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            Rating rating = ratingServiceRepository.findByCustomerIdAndOrderIdLimitOne(customerId,orderId);
            if (rating == null){
                log.warn("NOT DATA FOUND");
                return ResponseEntity.noContent().build();
            } else {
                log.info("SUCCESSFULLY FOUND DATA");
                return ResponseEntity.ok(new RatedOrder(rating.getCustomerId(),rating.getOrderId(),rating.getRatingValue()));
            }
        } catch (Exception ex){
            log.error("ERROR GETTING RATING FOR ORDER WITH ORDER_ID "+orderId+" FOR CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Scheduled(fixedRate = 100_000, initialDelay = 5_000)
    public void addProductRatingToExcel(){
        try{
            List<Rating> ratings = ratingServiceRepository.findAll();

            try{

                File file = new File("D:\\University Work\\FYP\\Easy Electro Shop\\Recommendation Models\\product_ratings.xlsx");

                // Create a FileInputStream object
                // for getting the information of the file
                FileInputStream fip = new FileInputStream(file);

                // Getting the workbook instance for XLSX file
                XSSFWorkbook workbook = new XSSFWorkbook(fip);

                Sheet sheet = workbook.getSheetAt(0);


                for ( int i = 0; i < ratings.size();i++){
                    try{
                        sheet.createRow((i+1)).createCell(0).setCellValue(String.valueOf(ratings.get(i).getCustomerId()));
                        sheet.getRow((i+1)).createCell(1).setCellValue(String.valueOf(ratings.get(i).getProductId()));
                        sheet.getRow((i+1)).createCell(2).setCellValue(ratings.get(i).getRatingValue());
                    } catch (Exception ex){
                        workbook.close();
                        log.error("ERROR WHILE WRITING DATA",ex);
                    }
                }

                // Save the changes
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                workbook.close();

                log.info("ORDER DATA ADDED TO EXCEL FILE SUCCESSFULLY");

            } catch (IOException e) {
                log.error("ERROR ADDING ORDER DATA TO EXCEL FILE",e);
            }


        } catch (Exception ex){
            log.error("ERROR ADDING DATA ",ex);
        }

    }


}
