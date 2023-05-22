package com.easyelectroshop.productmanagementservice.Service;

import com.easyelectroshop.productmanagementservice.Model.Product;
import com.easyelectroshop.productmanagementservice.Repository.ProductManagementRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ProductManagementService {

    @Autowired
    ProductManagementRepository productManagementRepository;

    @Autowired
    Date date;

    @Autowired
    SimpleDateFormat simpleDateFormat;

    public HttpStatusCode saveProduct(Product product){
        log.info("ADDING NEW PRODUCT WITH NAME "+product.getName());
        try{
            product.setLastUpdated(simpleDateFormat.format(date.getTime()));
            productManagementRepository.save(product);
            log.info("SUCCESSFULLY ADDED PRODUCT WITH NAME "+product.getName());
            return HttpStatusCode.valueOf(201);
        } catch (Exception ex){
            log.error("ERROR WHILE ADDING PRODUCT WITH NAME "+product.getName(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public Optional<Product> getProductById(UUID productId){
        log.info("GETTING PRODUCT WITH PRODUCT_ID "+productId);
        Optional<Product> product = productManagementRepository.findById(productId);
        if(product.isPresent()){
            log.info("SUCCESSFULLY RETRIEVED PRODUCT WITH PRODUCT_ID "+productId);
            return product;
        } else {
            log.error("COULD NOT FIND PRODUCT WITH PRODUCT_ID "+productId);
            return Optional.empty();
        }
    }

    public List<Product> getAllProducts(int pageNumber, int pageSize, String sortBy) {
        log.info("GETTING ALL PRODUCTS");
        try{
            if(pageSize == -1){
                pageSize = Integer.MAX_VALUE;
            }
            List<Product> products = productManagementRepository.findAllWithOnlyCoverImage(sortBy,pageSize,pageNumber);
            log.info("SUCCESSFULLY RETRIEVED PRODUCTS");
            return products;
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE PRODUCTS",ex);
            return null;
        }
    }

    public int getProductsCount() {
        log.info("GETTING PRODUCTS COUNT");
        try{
            int length = productManagementRepository.findAll().size();
            log.info("SUCCESSFULLY RETRIEVED PRODUCTS COUNT");
            return length;
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE PRODUCTS",ex);
            return 0;
        }
    }

    public HttpStatusCode updateProduct(Product product) {
        log.info("UPDATING PRODUCT WITH NAME "+product.getName());
        try{
            Optional<Product> tempProduct = productManagementRepository.findById(product.getProductId());
            if(tempProduct.isPresent()){
                productManagementRepository.save(product);
                log.info("SUCCESSFULLY EDITED PRODUCT WITH NAME "+product.getName());
                return HttpStatusCode.valueOf(202);
            } else {
                log.info("COULD NOT FIND GIVEN PRODUCT, ADDING NEW PRODUCT");
                productManagementRepository.save(product);
                log.info("SUCCESSFULLY ADDED NEW PRODUCT");
                return HttpStatusCode.valueOf(201);
            }
        } catch (Exception ex){
            log.error("ERROR EDITING PRODUCT WITH NAME "+product.getName(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }


    public HttpStatusCode deleteProduct(UUID productId){
        log.info("DELETING PRODUCT WITH PRODUCT_ID "+productId);
        try{
            Optional<Product> product = productManagementRepository.findById(productId);
            if(product.isPresent()){
                productManagementRepository.deleteById(productId);
                log.info("SUCCESSFULLY DELETED PRODUCT WITH PRODUCT_ID "+productId);
                return HttpStatusCode.valueOf(200);
            } else {
                log.error("COULD NOT DELETE PRODUCT WITH PRODUCT_ID "+productId+", PRODUCT NOT FOUND!");
                return HttpStatusCode.valueOf(404);
            }
        } catch (Exception ex){
            log.error("COULD NOT DELETE PRODUCT WITH PRODUCT_ID "+productId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }
}
