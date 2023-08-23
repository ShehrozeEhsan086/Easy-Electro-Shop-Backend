package com.easyelectroshop.customerservice.Controller;


import com.easyelectroshop.customerservice.DTO.Cart.Cart;
import com.easyelectroshop.customerservice.DTO.Customer.Customer;
import com.easyelectroshop.customerservice.Service.CartService;
import com.easyelectroshop.customerservice.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerServiceController {

    @Autowired
    CustomerService customerService;

    @Autowired
    CartService cartService;

    // ----------------  APIS FOR CUSTOMER MANAGEMENT SERVICE [[START]] --------------------

    @PostMapping("/management/add-customer")
    public ResponseEntity<HttpStatusCode> saveCustomer(@RequestBody Customer customer){
        return ResponseEntity.status(customerService.saveCustomer(customer)).build();
    }

    @GetMapping("/management/get-customer-by-id/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable UUID customerId){
        Customer customer = customerService.getCustomerById(customerId);
        return customer != null ? ResponseEntity.ok(customer) : ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("/management/get-customer-by-email/{customerEmail}")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String customerEmail){
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        return customer != null ? ResponseEntity.ok(customer) : ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("/management/get-all")
    public ResponseEntity<List<Customer>> getAllCustomers(
            @RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value="pageSize",defaultValue = "5",required = false) int pageSize,
            @RequestParam(value="sort",defaultValue = "lastUpdated",required = false) String sortBy
    ){
        List<Customer> customers = customerService.getAllCustomers(pageNumber,pageSize,sortBy);
        return customers != null ? ResponseEntity.ok(customers) : ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("/management/get-all-count")
    public ResponseEntity<Integer> getCustomerCount(){
        return ResponseEntity.ok(customerService.getAllCount());
    }

    @PutMapping("/management/update-customer")
    public ResponseEntity<HttpStatusCode> updateCustomer(@RequestBody Customer customer){
        return ResponseEntity.status(customerService.updateCustomer(customer)).build();
    }

    @PutMapping("/management/complete-customer-profile")
    public ResponseEntity<HttpStatusCode> completeCustomerProfile(@RequestBody Customer customer){
        return ResponseEntity.status(customerService.completeCustomerProfile(customer)).build();
    }

    @DeleteMapping("/management/delete-customer/{customerId}")
    public ResponseEntity<HttpStatusCode> deleteCustomer(@PathVariable UUID customerId){
        return ResponseEntity.status(customerService.deleteCustomer(customerId)).build();
    }

    // ----------------  APIS FOR CUSTOMER MANAGEMENT SERVICE [[END]] --------------------


    // ----------------  APIS FOR CART SERVICE [[START]] --------------------

    @PostMapping("/management/add-to-cart/{customerId}/{productId}/{quantity}")
    public ResponseEntity<HttpStatusCode> addProductToCart(@PathVariable UUID customerId,
                                                           @PathVariable UUID productId,
                                                           @PathVariable int quantity){
        return ResponseEntity.status(cartService.addProductToCart(customerId,productId,quantity)).build();
    }

    @PutMapping("/management/remove-item-from-cart/{customerId}/{productId}")
    public ResponseEntity<HttpStatusCode> removeItem(@PathVariable UUID customerId,
                                                     @PathVariable UUID productId){
        return ResponseEntity.status( cartService.removeItem(customerId,productId)).build();
    }

    @PutMapping("/management/reduce-item-quantity-from-cart/{customerId}/{productId}")
    public ResponseEntity<HttpStatusCode> reduceItem(@PathVariable UUID customerId,
                                                     @PathVariable UUID productId){
        return ResponseEntity.status(cartService.reduceItem(customerId,productId)).build();
    }

    @GetMapping("/management/get-customer-cart/{customerId}")
    public ResponseEntity<Cart> getCustomerCart(@PathVariable UUID customerId){
        Cart cart = cartService.getCustomerCart(customerId);
        return cart != null ? ResponseEntity.ok(cart) : ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("/management/cancel-customer-cart/{customerId}")
    public ResponseEntity<HttpStatusCode> cancelCustomerCart(@PathVariable UUID customerId){
        return ResponseEntity.status(cartService.cancelCart(customerId)).build();
    }

    @DeleteMapping("/management/delete-customer-cart/{customerId}")
    public ResponseEntity<HttpStatusCode> deleteCustomerCart(@PathVariable UUID customerId) {
        return ResponseEntity.status(cartService.deleteCart(customerId)).build();
    }

    // ----------------  APIS FOR CART SERVICE [[END]] --------------------
}