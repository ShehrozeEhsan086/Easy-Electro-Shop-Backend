package com.easyelectroshop.orderservice.Controller;

import com.easyelectroshop.orderservice.DTO.OrderGetByIdResponse.OrderSingleResponseEntity;
import com.easyelectroshop.orderservice.DTO.ResponseDTO.OrderGetAllResponseEntity;
import com.easyelectroshop.orderservice.Model.OrderEntity;
import com.easyelectroshop.orderservice.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order-service")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/add-order")
    public ResponseEntity<HttpStatusCode> saveOrder(@RequestBody OrderEntity orderEntity){
        return orderService.saveOrder(orderEntity);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<OrderGetAllResponseEntity>> getAll(@RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
                                                                  @RequestParam(value="pageSize",defaultValue = "10",required = false) int pageSize,
                                                                  @RequestParam(value="sort",defaultValue = "created_at",required = false) String sortBy){
        return orderService.getAllOrders(sortBy,pageSize,pageNumber);
    }

    @GetMapping("/get-all-by-customer-id/{customerId}")
    public ResponseEntity<List<OrderGetAllResponseEntity>> getByCustomerId(@PathVariable UUID customerId,
                                                                            @RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
                                                                            @RequestParam(value="pageSize",defaultValue = "10",required = false) int pageSize,
                                                                            @RequestParam(value="sort",defaultValue = "created_at",required = false) String sortBy){
        return orderService.getAllOrdersByCustomerId(customerId,sortBy,pageSize,pageNumber);
    }

    @PutMapping("/change-order-status/{orderId}/{status}")
    public ResponseEntity<HttpStatusCode> changeStatus(@PathVariable long orderId,
                                                       @PathVariable String status){
        return orderService.changeOrderStatus(orderId,status);
    }

    @DeleteMapping("/delete-order/{orderId}")
    public ResponseEntity<HttpStatusCode> deleteOrder(@PathVariable long orderId){
        return orderService.deleteOrder(orderId);
    }

    @PostMapping("/add-tracking-number/{orderId}/{shippingNumber}")
    public ResponseEntity<HttpStatusCode> addShippingNumber(@PathVariable long orderId,
                                                            @PathVariable String shippingNumber){
        return orderService.addShippingNumber(orderId,shippingNumber);
    }

    @GetMapping("/get-all-orders-count")
    public ResponseEntity<Long> getAllCount(){
        return orderService.getTotalOrdersCount();
    }

    @GetMapping("/get-pending-orders-count")
    public ResponseEntity<Long> getPendingCount(){
        return orderService.getPendingOrdersCount();
    }

    @GetMapping("/get-confirmed-orders-count")
    public ResponseEntity<Long> getConfirmedCount(){
        return orderService.getConfirmedOrdersCount();
    }

    @GetMapping("/get-readytoship-orders-count")
    public ResponseEntity<Long> getReadyToShipCount(){
        return orderService.getReadyToShipOrdersCount();
    }

    @GetMapping("/get-intransit-orders-count")
    public ResponseEntity<Long> getInTransitCount(){
        return orderService.getInTransitOrdersCount();
    }

    @GetMapping("/get-delivered-orders-count")
    public ResponseEntity<Long> getDeliveredCount(){
        return orderService.getDeliveredOrdersCount();
    }

    @GetMapping("/get-order-by-id/{orderId}")
    public ResponseEntity<OrderSingleResponseEntity> getOrderById(@PathVariable long orderId){
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/get-server-status")
    public ResponseEntity<HttpStatusCode> getStatus(){
        return ResponseEntity.ok(HttpStatusCode.valueOf(200));
    }

    @GetMapping("/get-order-count-by-customer-id/{customerId}")
    public ResponseEntity<Long> getOrderCountForCustomer(@PathVariable UUID customerId){
        return orderService.getOrderCountForCustomer(customerId);
    }

    @GetMapping("/get/{orderId}")
    public OrderEntity get(@PathVariable long orderId){
        return orderService.get(orderId);
    }

}