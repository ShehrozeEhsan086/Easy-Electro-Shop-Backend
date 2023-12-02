package com.easyelectroshop.orderservice.Service;

import com.easyelectroshop.orderservice.DTO.Customer.Address;
import com.easyelectroshop.orderservice.DTO.Customer.Customer;
import com.easyelectroshop.orderservice.DTO.OrderGetByIdResponse.AddressResponse;
import com.easyelectroshop.orderservice.DTO.OrderGetByIdResponse.CustomerResponse;
import com.easyelectroshop.orderservice.DTO.OrderGetByIdResponse.OrderDetailResponse;
import com.easyelectroshop.orderservice.DTO.OrderGetByIdResponse.OrderSingleResponseEntity;
import com.easyelectroshop.orderservice.DTO.OrdersInExcelSheet.ExcelSheetDTO;
import com.easyelectroshop.orderservice.DTO.ProductDTO.Product;
import com.easyelectroshop.orderservice.Model.OrderEntity;
import com.easyelectroshop.orderservice.Model.OrderContent;
import com.easyelectroshop.orderservice.DTO.ResponseDTO.OrderGetAllResponseEntity;
import com.easyelectroshop.orderservice.Respository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    @Value("${internal.communication.header.name}")
    private String headerName;

    @Value("${internal.communication.header.value}")
    private String headerValue;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    WebClient.Builder webClientBuilder;

    @Autowired
    DecimalFormat decimalFormat;

    public ResponseEntity<OrderEntity> saveOrder(OrderEntity orderEntity){
      try{
          log.info("ADDING ORDER FOR CUSTOMER WITH CUSTOMER_ID "+ orderEntity.getCustomerId());
          if( orderEntity.getTotalOrderPrice() == 0.0){
              double totalContentPrice = orderEntity.getTotalContentPrice();
              double shippingCost = orderEntity.getShippingCost();
              orderEntity.setTotalOrderPrice(totalContentPrice+shippingCost);
          }
          orderEntity.setCreatedAt(LocalDate.now());
          if(orderEntity.isOnlinePayment()){
              orderEntity.setOrderStatus("confirmed");
          } else {
              orderEntity.setOrderStatus("pending");
          }

          OrderEntity order = orderRepository.save(orderEntity);

          webClientBuilder.build()
                  .put()
                  .uri("http://customer-service/api/v1/customer/add-order-info/"+ orderEntity.getCustomerId()+"/"+ orderEntity.getTotalOrderPrice())
                  .retrieve()
                  .toBodilessEntity()
                  .flatMap(response -> Mono.just(response.getStatusCode()))
                  .block();

          log.info("SUCCESSFULLY ADDED ORDER FOR CUSTOMER WITH CUSTOMER_ID "+ orderEntity.getCustomerId());

          webClientBuilder.build()
                  .post()
                  .uri("http://analytics-service/api/v1/analytics-service/add-order-analytics")
                  .contentType(MediaType.APPLICATION_JSON)
                  .body(BodyInserters.fromValue(orderEntity))
                  .retrieve()
                  .toBodilessEntity()
                  .flatMap(response -> Mono.just(response.getStatusCode()))
                  .block();

          return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(order);
      } catch (Exception ex){
          log.error("ERROR ADDING ORDER FOR CUSTOMER WITH CUSTOMER_ID "+ orderEntity.getCustomerId(),ex);
          return ResponseEntity.internalServerError().build();
      }
    }

    public ResponseEntity<HttpStatusCode> changeOrderStatus(long orderId,String status){
        log.info("CHANGING ORDER INFO FOR ORDER WITH ORDER_ID "+orderId);
        try{
            Optional<OrderEntity> order = orderRepository.findById(orderId);
            if(order.isPresent()){
                 order.get().setOrderStatus(status);
                 orderRepository.save(order.get());
                 log.info("ORDER WITH ORDER_ID "+orderId+" STATUS CHANGED TO "+status);
                 return ResponseEntity.ok().build();
            } else {
                log.error("ORDER WITH ORDER_ID "+orderId+" NOT FOUND");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR CHANGING ORDER INFO FOR ORDER WITH ORDER_ID "+orderId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> deleteOrder(long orderId){
        log.info("DELETING ORDER WITH ORDER_ID "+orderId);
        try{
            Optional<OrderEntity> order = orderRepository.findById(orderId);
            if(order.isPresent()){
                if(order.get().getOrderStatus().equals("pending") || order.get().getOrderStatus().equals("processing") || order.get().getOrderStatus().equals("confirmed")){
                    log.info("ORDER ELIGIBLE FOR DELETION, DELETING ORDER FOR ORDER_ID "+orderId);
                    for (OrderContent orderContent : order.get().getOrderContent()) {
                        webClientBuilder.build()
                                .put()
                                .uri("http://product-service/api/v1/product/increase-product-stock/"+orderContent.getProductId()+"/"+orderContent.getQuantity())
                                .retrieve()
                                .toBodilessEntity()
                                .flatMap(response -> Mono.just(response.getStatusCode()))
                                .block();
                    }
                    webClientBuilder.build()
                            .put()
                            .uri("http://customer-service/api/v1/customer/remove-order-info/"+order.get().getCustomerId()+"/"+order.get().getTotalOrderPrice())
                            .retrieve()
                            .toBodilessEntity()
                            .flatMap(response -> Mono.just(response.getStatusCode()))
                            .block();
                    orderRepository.delete(order.get());
                    log.info("SUCCESSFULLY DELETE ORDER");
                    return ResponseEntity.ok().build();
                } else {
                    log.error("ORDER NOT ELIGIBLE FOR DELETION");
                    return ResponseEntity.status(HttpStatusCode.valueOf(406)).build();
                }
            } else {
                log.error("ORDER WITH ORDER_ID "+orderId+" NOT FOUND");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR DELETING ORDER WITH ORDER_ID "+orderId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public OrderEntity get(long orderId){
        return orderRepository.findById(orderId).get();
    }
    public ResponseEntity<OrderSingleResponseEntity> getOrderById(long orderId){
        log.info("GETTING ORDER WITH ORDER_ID "+orderId);
        try{
            Optional<OrderEntity> initialOrderObj = orderRepository.findById(orderId);
            if (initialOrderObj.isPresent()){
                log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO GET CUSTOMER WITH CUSTOMER_ID "+initialOrderObj.get().getCustomerId());
                Customer initialCustomer;
                try{
                    initialCustomer = webClientBuilder.build()
                            .get()
                            .uri("http://customer-management-service/api/v1/customer-management/get-customer-by-id/"+initialOrderObj.get().getCustomerId())
                            .header(headerName,headerValue)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .toEntity(Customer.class)
                            .block()
                            .getBody();
                }catch (Exception ex){
                    log.error("ERROR WHILE CALLING CUSTOMER MANAGEMENT SERVICE TO GET CUSTOMER WITH CUSTOMER_ID "+initialOrderObj.get().getCustomerId(), ex);
                    return ResponseEntity.internalServerError().build();
                }
                log.info("SUCCESSFULLY RETRIEVED INFORMATION FOR CUSTOMER WITH CUSTOMER_ID "+initialCustomer.customerId());
                AddressResponse customerAddress = new AddressResponse(initialCustomer.address().province(),initialCustomer.address().city(),initialCustomer.address().area(),initialCustomer.address().addressLine(),initialCustomer.address().postCode());
                CustomerResponse customer = new CustomerResponse(initialCustomer.fullName(),initialCustomer.email(),initialCustomer.phoneNumber(),initialCustomer.gender(),customerAddress);

                List<OrderDetailResponse> orderDetailList = new ArrayList<>();

                for (int i=0;i<initialOrderObj.get().getOrderContent().size();i++){
                    log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT WITH PRODUCT_ID "+initialOrderObj.get().getOrderContent().get(i).getProductId());
                    Product product;
                    try{
                        product = webClientBuilder.build()
                                .get()
                                .uri("http://product-management-service/api/v1/product-management/get-product/"+initialOrderObj.get().getOrderContent().get(i).getProductId())
                                .header(headerName,headerValue)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .toEntity(Product.class)
                                .block()
                                .getBody();
                    } catch (Exception ex){
                        log.error("ERROR ");
                        return ResponseEntity.internalServerError().build();
                    }
                    log.info("SUCCESSFULLY RETRIEVED INFORMATION FOR PRODUCT WITH PRODUCT_ID "+ product.productId());
                    OrderDetailResponse orderDetail = new OrderDetailResponse(product.coverImage(), product.name(), product.price(), initialOrderObj.get().getOrderContent().get(i).getQuantity(),(product.price() *  initialOrderObj.get().getOrderContent().get(i).getQuantity()));
                    orderDetailList.add(orderDetail);
                }

                OrderSingleResponseEntity orderResponse = new OrderSingleResponseEntity(orderId,orderDetailList,initialOrderObj.get().getTotalContentPrice(),initialOrderObj.get().getShippingCost(),initialOrderObj.get().getTotalOrderPrice(),initialOrderObj.get().getOrderStatus(),initialOrderObj.get().getShippingTrackingNumber(),customer);

                log.info("SUCCESSFULLY RETRIEVED ORDER INFO FOR ORDER WITH ORDER_ID "+orderId);

                return ResponseEntity.ok(orderResponse);

            } else {
                log.error("ORDER WITH ORDER_ID "+orderId+" NOT FOUND!");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR GETTING ORDER WITH ORDER_ID "+orderId);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<OrderGetAllResponseEntity>> getAllOrders(String sortBy, int pageSize, int pageNumber){
        log.info("GETTING ALL ORDERS");
        try{
            if(pageSize == -1){
                pageSize = (int) orderRepository.count();
            }
            Page<OrderEntity> retrievedOrders = orderRepository.findAll(PageRequest.of(pageNumber,pageSize, Sort.by(sortBy).descending()));
            List<OrderEntity> orders = retrievedOrders.getContent();
            List<OrderGetAllResponseEntity> responseOrders = new ArrayList<>();
            for(int i =0;i<orders.size();i++){
                log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO GET CUSTOMER WITH CUSTOMER_ID "+orders.get(i).getCustomerId());
                String fullName;
                try{
                    fullName = webClientBuilder.build()
                            .get()
                            .uri("http://customer-management-service/api/v1/customer-management/get-customer-name/"+orders.get(i).getCustomerId())
                            .header(headerName,headerValue)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .toEntity(String.class)
                            .block()
                            .getBody();

                } catch (Exception ex){
                    log.error("ERROR WHILE CALLING CUSTOMER MANAGEMENT SERVICE TO GET CUSTOMER WITH CUSTOMER_ID "+orders.get(i).getCustomerId()+" CHECK CUSTOMER MANAGEMENT LOG", ex);
                    return ResponseEntity.internalServerError().build();
                }
                log.info("SUCCESSFULLY RETRIEVED CUSTOMER_NAME "+fullName+" FOR CUSTOMER WITH CUSTOMER_ID "+orders.get(i).getCustomerId());
                List<String> productNames = new ArrayList<>();
                for(int j =0;j<orders.get(i).getOrderContent().size();j++){
                    log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT_NAME FOR PRODUCT WITH PRODUCT_ID "+orders.get(i).getOrderContent().get(j).getProductId());
                    String productName;
                    try{
                        productName = webClientBuilder.build()
                                .get()
                                .uri("http://product-management-service/api/v1/product-management/get-product-name/"+orders.get(i).getOrderContent().get(j).getProductId())
                                .header(headerName,headerValue)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .toEntity(String.class)
                                .block()
                                .getBody();
                    } catch (Exception ex){
                        log.error("ERROR CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT_NAME FOR PRODUCT WITH PRODUCT_ID "+orders.get(i).getOrderContent().get(j).getProductId());
                        return ResponseEntity.internalServerError().build();
                    }
                    log.info("SUCCESSFULLY RETRIEVED PRODUCT_NAME "+productName+" FOR PRODUCT WITH PRODUCT_ID "+orders.get(i).getOrderContent().get(j).getProductId());
                    productNames.add(productName);
                }

                OrderGetAllResponseEntity orderGetAllResponseEntity = new OrderGetAllResponseEntity(orders.get(i).getOrderId(),productNames,fullName,orders.get(i).getTotalOrderPrice(),orders.get(i).getCreatedAt(),orders.get(i).getOrderStatus(),orders.get(i).getShippingTrackingNumber());
                responseOrders.add(orderGetAllResponseEntity);
            }

            return ResponseEntity.ok(responseOrders);
        } catch (Exception ex){
            log.error("ERROR GETTING ALL ORDERS");
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<OrderGetAllResponseEntity>> getAllOrdersByCustomerId(UUID customerId, String sortBy, int pageSize, int pageNumber){
        log.info("GETTING ORDER INFO FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            if(pageSize == -1){
                pageSize = (int) orderRepository.count();
            }
            Page<OrderEntity> retrievedOrders = orderRepository.findAllByCustomerId(PageRequest.of(pageNumber,pageSize, Sort.by(sortBy).descending()),customerId);
            List<OrderEntity> orders = retrievedOrders.getContent();
            List<OrderGetAllResponseEntity> responseOrders = new ArrayList<>();
            for(int i =0;i< orders.size();i++){
                log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO GET CUSTOMER WITH CUSTOMER_ID "+orders.get(i).getCustomerId());
                String fullName;
                try{
                    fullName = webClientBuilder.build()
                            .get()
                            .uri("http://customer-management-service/api/v1/customer-management/get-customer-name/"+orders.get(i).getCustomerId())
                            .header(headerName,headerValue)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .toEntity(String.class)
                            .block()
                            .getBody();

                } catch (Exception ex){
                    log.error("ERROR WHILE CALLING CUSTOMER MANAGEMENT SERVICE TO GET CUSTOMER WITH CUSTOMER_ID "+orders.get(i).getCustomerId()+" CHECK CUSTOMER MANAGEMENT LOG", ex);
                    return ResponseEntity.internalServerError().build();
                }
                log.info("SUCCESSFULLY RETRIEVED CUSTOMER_NAME "+fullName+" FOR CUSTOMER WITH CUSTOMER_ID "+orders.get(i).getCustomerId());
                List<String> productNames = new ArrayList<>();
                for(int j =0;j<orders.get(i).getOrderContent().size();j++){
                    log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT_NAME FOR PRODUCT WITH PRODUCT_ID "+orders.get(i).getOrderContent().get(j).getProductId());
                    String productName;
                    try{
                        productName = webClientBuilder.build()
                                .get()
                                .uri("http://product-management-service/api/v1/product-management/get-product-name/"+orders.get(i).getOrderContent().get(j).getProductId())
                                .header(headerName,headerValue)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .toEntity(String.class)
                                .block()
                                .getBody();
                    } catch (Exception ex){
                        log.error("ERROR CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT_NAME FOR PRODUCT WITH PRODUCT_ID "+orders.get(i).getOrderContent().get(j).getProductId());
                        return ResponseEntity.internalServerError().build();
                    }
                    log.info("SUCCESSFULLY RETRIEVED PRODUCT_NAME "+productName+" FOR PRODUCT WITH PRODUCT_ID "+orders.get(i).getOrderContent().get(j).getProductId());
                    productNames.add(productName);
                }

                OrderGetAllResponseEntity orderGetAllResponseEntity = new OrderGetAllResponseEntity(orders.get(i).getOrderId(),productNames,fullName,orders.get(i).getTotalOrderPrice(),orders.get(i).getCreatedAt(),orders.get(i).getOrderStatus(),orders.get(i).getShippingTrackingNumber());
                responseOrders.add(orderGetAllResponseEntity);
            }

            return ResponseEntity.ok(responseOrders);
        } catch (Exception ex){
            log.error("ERROR GETTING ALL ORDERS",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> addShippingNumber(long orderId,String shippingNumber){
        log.info("ADDING SHIPPING NUMBER "+shippingNumber+" TO ORDER WITH ORDER_ID");
        try{
            Optional<OrderEntity> order = orderRepository.findById(orderId);
            if(order.isPresent()){
                order.get().setShippingTrackingNumber(shippingNumber);
                orderRepository.save(order.get());
                log.info("SUCCESSFULLY ADDED SHIPPING NUMBER "+shippingNumber+" TO ORDER WITH ORDER_ID");
                return ResponseEntity.ok().build();
            } else {
                log.error("ORDER WITH ORDER_ID "+orderId+" NOT FOUND");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR ADDING SHIPPING NUMBER "+shippingNumber+" TO ORDER WITH ORDER_ID",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getOrderCountForCustomer(UUID customerId){
        log.info("GETTING ALL ORDERS COUNT FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            long count = orderRepository.countByCustomerId(customerId);
            log.info("SUCCESSFULLY RETRIEVED ORDER COUNT VALUE: "+count);
            return ResponseEntity.ok(count);
        } catch (Exception ex){
            log.error("ERROR GETTING ALL ORDERS COUNT FOR CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getTotalOrdersCount(){
        log.info("GETTING ALL ORDERS COUNT");
        try{
            long count = orderRepository.count();
            log.info("SUCCESSFULLY RETRIEVED ORDERS COUNT VALUE: "+count);
            return ResponseEntity.ok(count);
        } catch (Exception ex){
            log.error("ERROR GETTING ALL ORDERS COUNT");
            return  ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getPendingOrdersCount(){
        log.info("GETTING ALL PENDING ORDERS COUNT");
        try{
            long count = orderRepository.countByOrderStatus("pending");
            log.info("SUCCESSFULLY RETRIEVED PENDING ORDERS COUNT VALUE: "+count);
            return ResponseEntity.ok(count);
        } catch (Exception ex){
            log.error("ERROR GETTING ALL PENDING ORDERS COUNT");
            return  ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getConfirmedOrdersCount(){
        log.info("GETTING ALL CONFIRMED ORDERS COUNT");
        try{
            long count = orderRepository.countByOrderStatus("confirmed");
            log.info("SUCCESSFULLY RETRIEVED CONFIRMED ORDERS COUNT VALUE: "+count);
            return ResponseEntity.ok(count);
        } catch (Exception ex){
            log.error("ERROR GETTING ALL CONFIRMED ORDERS COUNT");
            return  ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getReadyToShipOrdersCount(){
        log.info("GETTING ALL READY TO SHIP ORDERS COUNT");
        try{
            long count = orderRepository.countByOrderStatus("readytoship");
            log.info("SUCCESSFULLY RETRIEVED READY TO SHIP ORDERS COUNT VALUE: "+count);
            return ResponseEntity.ok(count);
        } catch (Exception ex){
            log.error("ERROR GETTING ALL READY TO SHIP ORDERS COUNT");
            return  ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getInTransitOrdersCount(){
        log.info("GETTING ALL IN TRANSIT ORDERS COUNT");
        try{
            long count = orderRepository.countByOrderStatus("intransit");
            log.info("SUCCESSFULLY RETRIEVED IN TRANSIT ORDERS COUNT VALUE: "+count);
            return ResponseEntity.ok(count);
        } catch (Exception ex){
            log.error("ERROR GETTING ALL IN TRANSIT ORDERS COUNT");
            return  ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getDeliveredOrdersCount(){
        log.info("GETTING ALL DELIVERED(COMPLETED) ORDERS COUNT");
        try{
            long count = orderRepository.countByOrderStatus("delivered");
            log.info("SUCCESSFULLY RETRIEVED DELIVERED(COMPLETED) ORDERS COUNT VALUE: "+count);
            return ResponseEntity.ok(count);
        } catch (Exception ex){
            log.error("ERROR GETTING ALL DELIVERED(COMPLETED) ORDERS COUNT");
            return  ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<String> getTotalSalesPrice(){
        log.info("GETTING TOTAL SALES AMOUNT");
        try{
            double totalSalesPrice = 0;
            List<OrderEntity> allDeliveredOrders = orderRepository.findAllByOrderStatus("delivered");
            if (allDeliveredOrders.isEmpty()){
                log.info("ZERO DELIVERED ORDERS");
                return ResponseEntity.ok("0");
            } else {
                for (OrderEntity allDeliveredOrder : allDeliveredOrders) {
                    totalSalesPrice = totalSalesPrice + (allDeliveredOrder.getTotalContentPrice());
                }
                log.info("SUCCESSFULLY RETRIEVED SALES AMOUNT "+totalSalesPrice);
                String formattedSalesPrice = decimalFormat.format(totalSalesPrice);
                return ResponseEntity.ok(formattedSalesPrice);
            }
        } catch (Exception ex){
            log.error("ERROR GETTING TOTAL SALES AMOUNT");
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<String> getTotalOnHoldPrice() {
        log.info("GETTING TOTAL ON HOLD AMOUNT");
        try{
            double totalOnHoldPrice = 0;
            List<OrderEntity> allNotDeliveredOrders = orderRepository.findAllByOrderStatusIsNot("delivered");
            if (allNotDeliveredOrders.isEmpty()){
                log.info("NO ORDERS FOUND");
                return ResponseEntity.ok("0");
            } else {
                for(OrderEntity notDeliveredOrder : allNotDeliveredOrders){
                    totalOnHoldPrice = totalOnHoldPrice + notDeliveredOrder.getTotalContentPrice();
                }
                log.info("SUCCESSFULLY RETRIEVED ON HOLD AMOUNT "+totalOnHoldPrice);
                String formattedOnHoldAmount = decimalFormat.format(totalOnHoldPrice);
                return ResponseEntity.ok(formattedOnHoldAmount);
            }
        } catch (Exception ex){
            log.error("ERROR GETTING TOTAL ON HOLD AMOUNT",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<OrderEntity> getOrderByIdOGObject(long orderId) {
        try{
            return ResponseEntity.ok(orderRepository.findById(orderId).get());
        } catch (Exception ex){
            return ResponseEntity.internalServerError().build();
        }
    }


//    @Scheduled(fixedRate = 100_000)
    @Scheduled(fixedRate = 100_000,initialDelay = 5000) // Initial 5 secs Delay and ran after every 100 secs
    public void updateOrderDataInExcelSheet(){
        try{
            log.info("UPDATING ORDER DATA IN EXCEL SHEET");

            List<OrderEntity> allOrders = orderRepository.findAll();

            List<ExcelSheetDTO> excelSheetDTOs = new ArrayList<>();

            for(int i = 0; i < allOrders.size() ; i++){
                if (allOrders.get(i).getOrderContent().size() <= 1){
                    ExcelSheetDTO excelSheetDTO = new ExcelSheetDTO(allOrders.get(i).getOrderId(),allOrders.get(i).getOrderContent().get(0).getProductId(),allOrders.get(i).getOrderContent().get(0).getQuantity());
                    excelSheetDTOs.add(excelSheetDTO);
                } else {
                    for(int j=0;j<allOrders.get(i).getOrderContent().size();j++){
                        ExcelSheetDTO excelSheetDTO = new ExcelSheetDTO(allOrders.get(i).getOrderId(),allOrders.get(i).getOrderContent().get(j).getProductId(),allOrders.get(i).getOrderContent().get(j).getQuantity());
                        excelSheetDTOs.add(excelSheetDTO);
                    }
                }
            }


            try{

            File file = new File("C:\\Users\\shehr\\OneDrive\\Desktop\\Easy-Electro-Shop\\Recommendation Models\\orders_data.xlsx");

            // Create a FileInputStream object
            // for getting the information of the file
            FileInputStream fip = new FileInputStream(file);

            // Getting the workbook instance for XLSX file
            XSSFWorkbook workbook = new XSSFWorkbook(fip);

                Sheet sheet = workbook.getSheetAt(0);


                for ( int i = 0; i < excelSheetDTOs.size();i++){
                    try{
                        sheet.createRow((i+1)).createCell(0).setCellValue(excelSheetDTOs.get(i).orderId());
                        sheet.getRow((i+1)).createCell(1).setCellValue(String.valueOf(excelSheetDTOs.get(i).productId()));
                        sheet.getRow((i+1)).createCell(2).setCellValue(excelSheetDTOs.get(i).quantity());
                    } catch (Exception exception){
                        log.error("ERROR WRITING DATA TO EXCEL FILE");
                        workbook.close();
                    }
                }
                // Save the changes
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                workbook.close();

                log.info("ORDER DATA ADDED TO EXCEL FILE SUCCESSFULLY");

            } catch (IOException e) {
                log.error("ERROR ADDING ORDER DATA TO EXCEL FILE");
            }

        } catch (Exception ex){
            log.error("THERE WAS AN UNEXPECTED ERROR ",ex);
        }
    }

    private Workbook getWorkbook(String filePath) throws IOException {
        return WorkbookFactory.create(getClass().getClassLoader().getResourceAsStream(filePath));
    }





}