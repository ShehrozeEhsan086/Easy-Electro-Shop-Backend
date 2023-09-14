package com.easyelectroshop.orderservice.Controller;

import com.easyelectroshop.orderservice.Model.OrderEntity;
import com.easyelectroshop.orderservice.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<OrderEntity>> getAll(@RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
                                                    @RequestParam(value="pageSize",defaultValue = "10",required = false) int pageSize,
                                                    @RequestParam(value="sort",defaultValue = "created_at",required = false) String sortBy){
        return orderService.getAllOrders(sortBy,pageSize,pageNumber);
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

}