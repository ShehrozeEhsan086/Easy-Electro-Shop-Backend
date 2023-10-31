package com.easyelectroshop.analyticsservice.Service;

import com.easyelectroshop.analyticsservice.DTO.Customer.Customer;
import com.easyelectroshop.analyticsservice.DTO.Order.OrderContent;
import com.easyelectroshop.analyticsservice.DTO.Order.OrderEntity;
import com.easyelectroshop.analyticsservice.DTO.ServiceStatus;
import com.easyelectroshop.analyticsservice.Model.CategorySales;
import com.easyelectroshop.analyticsservice.Model.CitySales;
import com.easyelectroshop.analyticsservice.Model.ProductSales;
import com.easyelectroshop.analyticsservice.Model.ProvinceSales;
import com.easyelectroshop.analyticsservice.Respository.CategorySalesRepository;
import com.easyelectroshop.analyticsservice.Respository.CitySalesRepository;
import com.easyelectroshop.analyticsservice.Respository.ProductSalesRepository;
import com.easyelectroshop.analyticsservice.Respository.ProvinceSalesRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Slf4j
public class AnalyticsService {

    @Autowired
    WebClient.Builder webClientBuilder;

    @Autowired
    ProductSalesRepository productSalesRepository;

    @Autowired
    ProvinceSalesRepository provinceSalesRepository;

    @Autowired
    CategorySalesRepository categorySalesRepository;

    @Autowired
    CitySalesRepository citySalesRepository;

    public ResponseEntity<ServiceStatus> getServiceStatus(String serviceName){
        log.info("CALLING "+serviceName+" TO GET SERVICE STATUS");
        try{
            Object orderServiceResponse = webClientBuilder.build()
                    .get()
                    .uri("http://"+serviceName+"/actuator/health")
                    .retrieve()
                    .toEntity(Object.class)
                    .block()
                    .getBody();

            ServiceStatus status = new ServiceStatus((String) PropertyUtils.getProperty(orderServiceResponse,"status"));
            log.info("RETURNED STATUS: "+status);
            return ResponseEntity.ok(status);
        } catch (Exception ex){
            log.error("ERROR", ex);
            ServiceStatus status = new ServiceStatus("DOWN");
            return ResponseEntity.ok(status);
        }
    }

    public ResponseEntity<String> getTotalInventoryPrice(){
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET TOTAL INVENTORY PRICE");
        try{
            String totalInventoryPrice = webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-total-inventory-price")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class)
                    .block()
                    .getBody();
            log.info("SUCCESSFULLY RETRIEVED TOTAL INVENTORY PRICE VALUE: "+totalInventoryPrice);
            return ResponseEntity.ok(totalInventoryPrice);
        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<String> getTotalSoldPrice() {
        log.info("CALLING ORDER SERVICE TO GET TOTAL SALES AMOUNT");
        try{
            String totalSalesPrice = webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-total-sales-price")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class)
                    .block()
                    .getBody();
            log.info("SUCCESSFULLY RETRIEVED TOTAL SALES AMOUNT VALUE: "+totalSalesPrice);
            return ResponseEntity.ok(totalSalesPrice);
        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<String> getTotalOnHoldAmount() {
        log.info("CALLING ORDER SERVICE TO GET TOTAL ON HOLD AMOUNT");
        try{
            String totalOnHoldPrice = webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-total-amount-on-hold")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class)
                    .block()
                    .getBody();
            log.info("SUCCESSFULLY RETRIEVED TOTAL ON HOLD AMOUNT VALUE: "+totalOnHoldPrice);
            return ResponseEntity.ok(totalOnHoldPrice);
        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Integer> getTotalCustomersCount() {
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO GET TOTAL CUSTOMERS COUNT");
        try{
            int totalCustomersCount = webClientBuilder.build()
                    .get()
                    .uri("http://customer-management-service/api/v1/customer-management/get-all-count")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(Integer.class)
                    .block()
                    .getBody();
            log.info("SUCCESSFULLY RETRIEVED TOTAL CUSTOMER COUNT VALUE: "+totalCustomersCount);
            return ResponseEntity.ok(totalCustomersCount);
        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Integer> getTotalProductsCount() {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET TOTAL PRODUCTS COUNT");
        try{
            int totalProductsCount = webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-all-count")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(Integer.class)
                    .block()
                    .getBody();
            log.info("SUCCESSFULLY RETRIEVED TOTAL PRODUCTS COUNT VALUE: "+totalProductsCount);
            return ResponseEntity.ok(totalProductsCount);
        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getTotalOrdersCount() {
        log.info("CALLING ORDER SERVICE TO GET TOTAL ORDERS COUNT");
        try{
            Long totalOrdersCount = webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-all-orders-count")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(Long.class)
                    .block()
                    .getBody();
            log.info("SUCCESSFULLY RETRIEVED TOTAL ORDERS COUNT VALUE: "+totalOrdersCount);
            return ResponseEntity.ok(totalOrdersCount);
        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> calculateAnalyticsForSoldProduct(OrderEntity order){
        log.info("ADDING INFO RELATED TO SOLD PRODUCTS");
        try {

            for(int i=0;i<order.orderContent().size();i++){

                ProductSales productSalesData = productSalesRepository.findByProductId(order.orderContent().get(i).productId());

                if(productSalesData != null){
                    long newQuantity = productSalesData.getSalesCount() + order.orderContent().get(i).quantity();
                    productSalesData.setSalesCount(newQuantity);
                    productSalesRepository.save(productSalesData);
                } else {
                    ProductSales newProductSalesData = new ProductSales();
                    newProductSalesData.setProductId(order.orderContent().get(i).productId());
                    newProductSalesData.setSalesCount(order.orderContent().get(i).quantity());
                    productSalesRepository.save(newProductSalesData);
                }

                long productCategory = webClientBuilder.build()
                        .get()
                        .uri("http://product-management-service/api/v1/product-management/get-category-by-product-id/"+order.orderContent().get(i).productId())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntity(Long.class)
                        .block()
                        .getBody();

                CategorySales categorySalesData = categorySalesRepository.findByCategoryId(productCategory);

                if(categorySalesData != null){
                    long newSalesValue = categorySalesData.getSalesCount() + 1;
                    categorySalesData.setSalesCount(newSalesValue);
                    categorySalesRepository.save(categorySalesData);
                } else {
                    CategorySales newCategorySalesData = new CategorySales();
                    newCategorySalesData.setCategoryId(productCategory);
                    newCategorySalesData.setSalesCount(1);
                    categorySalesRepository.save(newCategorySalesData);
                }
            }

            Customer customerData = webClientBuilder.build()
                    .get()
                    .uri("http://customer-management-service/api/v1/customer-management/get-customer-by-id/"+order.customerId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(Customer.class)
                    .block()
                    .getBody();

            ProvinceSales provinceSalesData = provinceSalesRepository.findByProvince(customerData.address().province());

            if(provinceSalesData != null){
                long newProvinceSalesValue = provinceSalesData.getSalesCount() + 1;
                provinceSalesData.setSalesCount(newProvinceSalesValue);
                provinceSalesRepository.save(provinceSalesData);
            } else {
                ProvinceSales newProvinceSalesData = new ProvinceSales();
                newProvinceSalesData.setProvince(customerData.address().province());
                newProvinceSalesData.setSalesCount(1);
                provinceSalesRepository.save(newProvinceSalesData);
            }

            CitySales citySalesData = citySalesRepository.findByCity(customerData.address().city());

            if(citySalesData != null){
                long newCitySalesValue = citySalesData.getSalesCount() + 1;
                citySalesData.setSalesCount(newCitySalesValue);
                citySalesRepository.save(citySalesData);
            } else {
                CitySales newCitySalesData = new CitySales();
                newCitySalesData.setCity(customerData.address().city());
                newCitySalesData.setSalesCount(1);
                citySalesRepository.save(newCitySalesData);
            }

            log.info("SUCCESSFULLY ADDED ANALYTICS FOR ORDER WITH ORDER_ID "+order.orderId());
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).build();

        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.internalServerError().build();
        }
    }
}