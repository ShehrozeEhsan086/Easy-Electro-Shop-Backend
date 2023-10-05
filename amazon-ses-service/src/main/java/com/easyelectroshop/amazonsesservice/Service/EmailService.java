package com.easyelectroshop.amazonsesservice.Service;

import com.easyelectroshop.amazonsesservice.Model.Customer.Customer;
import com.easyelectroshop.amazonsesservice.Model.EmailData.EmailData;
import com.easyelectroshop.amazonsesservice.Model.EmailData.OrderDetail;
import com.easyelectroshop.amazonsesservice.Model.Order.OrderEntity;
import com.easyelectroshop.amazonsesservice.Model.ProductDTO.Product;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.*;

@Service
@Slf4j
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${fromEmail}")
    private String fromEmail;

    @Value("${fromName}")
    private String fromName;

    @Autowired
    SpringTemplateEngine templateEngine;

    @Value("${internal.communication.header.name}")
    private String headerName;

    @Value("${internal.communication.header.value}")
    private String headerValue;

    @Autowired
    WebClient.Builder webClientBuilder;

    @Async
    public void htmlSend(OrderEntity order) {
        log.info("STARTING EMAIL SENDING PROCESS FOR ORDER WITH ORDER_ID "+order.orderId());
        try{

            UUID customerId = order.customerId();

            log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO GET CUSTOMER WITH CUSTOMER_ID "+customerId);


            Customer customer = webClientBuilder.build()
                        .get()
                        .uri("http://customer-management-service/api/v1/customer-management/get-customer-by-id/"+customerId)
                        .header(headerName,headerValue)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntity(Customer.class)
                        .block()
                        .getBody();

            log.info("SUCCESSFULLY RETRIEVED CUSTOMER INFORMATION WITH EMAIL "+customer.email());

            List<OrderDetail> orderDetailList = new ArrayList<>();

            log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT DETAILS");

            for (int i =0;i<order.orderContent().size();i++){
                log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT WITH PRODUCT_ID "+order.orderContent().get(i).productId());
                Product product = webClientBuilder.build()
                            .get()
                            .uri("http://product-management-service/api/v1/product-management/get-product/"+order.orderContent().get(i).productId())
                            .header(headerName,headerValue)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .toEntity(Product.class)
                            .block()
                            .getBody();

                log.info("SUCCESSFULLY RETRIEVED INFORMATION FOR PRODUCT WITH PRODUCT_ID "+ product.productId());

                OrderDetail orderDetail;

                if(product.isDiscounted()){
                    orderDetail = new OrderDetail(product.coverImage(), product.name(), product.discountedPrice(), order.orderContent().get(i).quantity(),(product.discountedPrice() * order.orderContent().get(i).quantity()));

                } else {
                    orderDetail = new OrderDetail(product.coverImage(), product.name(), product.price(), order.orderContent().get(i).quantity(),(product.price() * order.orderContent().get(i).quantity()));
                }
                orderDetailList.add(orderDetail);
            }

            log.info(orderDetailList.get(0).coverImage());

            EmailData emailData = new EmailData(
                    customer.fullName(),
                    order.orderId(),
                    orderDetailList,
                    order.totalContentPrice(),
                    order.shippingCost(),
                    order.totalOrderPrice(),
                    order.orderStatus(),
                    customer.address());

            log.info("SENDING EMAIL TO CUSTOMER WITH CUSTOMER_EMAIL "+customer.email()+" OF ORDER WITH ORDER_ID "+order.orderId());


            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(fromEmail,fromName);
            helper.setTo(customer.email());
            helper.setSubject("Order Received");

            Context context = new Context();
            Map<String, Object> properties = new HashMap<String,Object>();
            properties.put("customerName",customer.fullName());
            properties.put("orderId",order.orderId());
            properties.put("orderStatus",order.orderStatus());
            properties.put("coverImage",orderDetailList.get(0).coverImage());
            properties.put("productName",orderDetailList.get(0).productName());
            properties.put("price",orderDetailList.get(0).price());
            properties.put("quantity",orderDetailList.get(0).quantity());
            properties.put("totalPrice",orderDetailList.get(0).totalPrice());

            context.setVariables(properties);

            String html = templateEngine.process("emails/OrderMail.html",context);

            helper.setText(html,true);

            javaMailSender.send(message);
        } catch (Exception ex){
            log.error("ERROR ",ex);
        }
    }

//    public ResponseEntity<HttpStatusCode> sendOrderEmail(OrderEntity order){
//        log.info("STARTING EMAIL SENDING PROCESS FOR ORDER WITH ORDER_ID "+order.orderId());
//        try{
//            UUID customerId = order.customerId();
//
//            log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO GET CUSTOMER WITH CUSTOMER_ID "+customerId);
//
//            Customer customer;
//
//            try{
//                customer = webClientBuilder.build()
//                        .get()
//                        .uri("http://customer-management-service/api/v1/customer-management/get-customer-by-id/"+customerId)
//                        .header(headerName,headerValue)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .retrieve()
//                        .toEntity(Customer.class)
//                        .block()
//                        .getBody();
//
//            }catch (Exception ex){
//                log.error("ERROR WHILE CALLING CUSTOMER MANAGEMENT SERVICE TO GET CUSTOMER WITH CUSTOMER_ID "+customerId, ex);
//                return ResponseEntity.internalServerError().build();
//            }
//
//            log.info("SUCCESSFULLY RETRIEVED CUSTOMER INFORMATION WITH EMAIL "+customer.email());
//
//            List<OrderDetail> orderDetailList = new ArrayList<>();
//
//            log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT DETAILS");
//
//            for (int i =0;i<order.orderContent().size();i++){
//                log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT WITH PRODUCT_ID "+order.orderContent().get(i).productId());
//                Product product;
//                try{
//                    product = webClientBuilder.build()
//                            .get()
//                            .uri("http://product-management-service/api/v1/product-management/get-product/"+order.orderContent().get(i).productId())
//                            .header(headerName,headerValue)
//                            .accept(MediaType.APPLICATION_JSON)
//                            .retrieve()
//                            .toEntity(Product.class)
//                            .block()
//                            .getBody();
//                } catch (Exception ex){
//                    log.error("ERROR CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT WITH PRODUCT_ID "+order.orderContent().get(i).productId(),ex);
//                    return ResponseEntity.internalServerError().build();
//                }
//                log.info("SUCCESSFULLY RETRIEVED INFORMATION FOR PRODUCT WITH PRODUCT_ID "+ product.productId());
//
//                OrderDetail orderDetail;
//
//                if(product.isDiscounted()){
//                    orderDetail = new OrderDetail("COVER IMAGE", product.name(), product.discountedPrice(), order.orderContent().get(i).quantity(),(product.discountedPrice() * order.orderContent().get(i).quantity()));
//
//                } else {
//                    orderDetail = new OrderDetail("COVER IMAGE", product.name(), product.price(), order.orderContent().get(i).quantity(),(product.price() * order.orderContent().get(i).quantity()));
//                }
//                orderDetailList.add(orderDetail);
//            }
//
//            EmailData emailData = new EmailData(
//                    customer.fullName(),
//                    order.orderId(),
//                    orderDetailList,
//                    order.totalContentPrice(),
//                    order.shippingCost(),
//                    order.totalOrderPrice(),
//                    order.orderStatus(),
//                    customer.address());
//
//            log.info("SENDING EMAIL TO CUSTOMER WITH CUSTOMER_EMAIL "+customer.email()+" OF ORDER WITH ORDER_ID "+order.orderId());
//
//            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//            simpleMailMessage.setFrom(senderEmail);
//            simpleMailMessage.setTo(customer.email());
//            simpleMailMessage.setSubject("Order Number "+order.orderId()+" Received.");
//            simpleMailMessage.setText(emailData.toString());
//            mailSender.send(simpleMailMessage);
//
//            log.info("SUCCESSFULLY SENT EMAIL TO CUSTOMER WITH CUSTOMER_EMAIL "+customer.email()+" OF ORDER WITH ORDER_ID "+order.orderId());
//
//            return ResponseEntity.ok().build();
//
//        } catch (Exception ex){
//            log.error("ERROR SENDING EMAIL FOR ORDER WITH ORDER_ID "+order.orderId());
//            return ResponseEntity.internalServerError().build();
//        }
//    }
}