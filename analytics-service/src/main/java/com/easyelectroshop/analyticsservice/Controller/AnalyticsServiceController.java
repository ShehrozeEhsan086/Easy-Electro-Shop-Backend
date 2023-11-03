package com.easyelectroshop.analyticsservice.Controller;

import com.easyelectroshop.analyticsservice.DTO.Customer.Customer;
import com.easyelectroshop.analyticsservice.DTO.Order.OrderEntity;
import com.easyelectroshop.analyticsservice.DTO.Product.ProductResponse;
import com.easyelectroshop.analyticsservice.DTO.ProductCategory.CategoryResponse;
import com.easyelectroshop.analyticsservice.DTO.Province.ProvinceResponse;
import com.easyelectroshop.analyticsservice.DTO.ServiceStatus;
import com.easyelectroshop.analyticsservice.Model.CitySales;
import com.easyelectroshop.analyticsservice.Service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics-service")
public class AnalyticsServiceController {

    @Autowired
    AnalyticsService analyticsService;

    @GetMapping("/get-service-status")
    public ResponseEntity<ServiceStatus> getServiceStatus(@RequestParam (value="serviceName",required = true) String serviceName){
        return analyticsService.getServiceStatus(serviceName);
    }

    @GetMapping("/get-total-inventory-price")
    public ResponseEntity<String> getTotalInventoryPrice(){
        return analyticsService.getTotalInventoryPrice();
    }

    @GetMapping("/get-total-sold-price")
    public ResponseEntity<String> getTotalSalesPrice(){
        return analyticsService.getTotalSoldPrice();
    }

    @GetMapping("/get-total-amount-on-hold")
    public ResponseEntity<String> getTotalOnHoldPrice(){
        return analyticsService.getTotalOnHoldAmount();
    }

    @GetMapping("/get-total-customers-count")
    public ResponseEntity<Integer> getTotalCustomersCount(){
        return analyticsService.getTotalCustomersCount();
    }

    @GetMapping("/get-total-products-count")
    public ResponseEntity<Integer> getTotalProductsCount(){
        return analyticsService.getTotalProductsCount();
    }

    @GetMapping("/get-total-orders-count")
    public ResponseEntity<Long> getTotalOrdersCount(){
        return analyticsService.getTotalOrdersCount();
    }

    @PostMapping("/add-order-analytics")
    public ResponseEntity<HttpStatusCode> addOrderAnalytics(@RequestBody OrderEntity order){
        return analyticsService.calculateAnalyticsForSoldProduct(order);
    }

    @GetMapping("/get-category-sales-data")
    public ResponseEntity<List<CategoryResponse>> getCategorySales(){
        return analyticsService.getTopCategorySales();
    }

    @GetMapping("/get-city-sales-data")
    public ResponseEntity<List<CitySales>> getCitySales(){
        return analyticsService.getTopCitySales();
    }

    @GetMapping("/get-products-sales-data")
    public ResponseEntity<List<ProductResponse>> getProductSales(){
        return analyticsService.getTopProductSales();
    }

    @GetMapping("/get-province-sales-data")
    public ResponseEntity<List<ProvinceResponse>> getProvinceSales(){
        return analyticsService.getProvinceSales();
    }

    @GetMapping("/get-top-customers")
    public ResponseEntity<List<Customer>> getTop5CustomersWithMostOrders(){
        return analyticsService.getTop5CustomersWithMostOrders();
    }

}