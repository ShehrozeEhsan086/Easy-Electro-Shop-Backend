package com.easyelectroshop.productmanagementservice.Service;

import com.easyelectroshop.productmanagementservice.Model.Product;
import com.easyelectroshop.productmanagementservice.Repository.ProductManagementRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class ProductManagementService {

    @Autowired
    ProductManagementRepository productManagementRepository;
    @Autowired
    Date date;
    @Autowired
    SimpleDateFormat simpleDateFormat;


    public boolean saveProduct(Product product){
        log.info("ADDING NEW PRODUCT WITH NAME "+product.getName());
        try{
            product.setLastUpdated(simpleDateFormat.format(date.getTime()));
            productManagementRepository.save(product);
            log.info("SUCCESSFULLY ADDED PRODUCT WITH NAME "+product.getName());
            return true;
        } catch (Exception ex){
            log.error("ERROR WHILE ADDING PRODUCT WITH NAME "+product.getName(),ex);
            return false;
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
            Page<Product> productPage = productManagementRepository.findAll(PageRequest.of(pageNumber,pageSize, Sort.by(sortBy)));
            List<Product> products = productPage.toList();
            log.info("SUCCESSFULLY RETRIEVED PRODUCTS");
            return products;
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE PRODUCTS",ex);
            return null;
        }
    }

    public boolean updateProduct(Product product) {
        log.info("UPDATING PRODUCT WITH NAME "+product.getName());
        try{
            Optional<Product> tempProduct = productManagementRepository.findById(product.getProductId());
            if(tempProduct.isPresent()){
                productManagementRepository.save(product);
                log.info("SUCCESSFULLY EDITED PRODUCT WITH NAME "+product.getName());
                return true;
            } else {
                log.info("COULD NOT FIND GIVEN PRODUCT, ADDING NEW PRODUCT");
                productManagementRepository.save(product);
                log.info("SUCCESSFULLY ADDED NEW PRODUCT");
                return true;
            }
        } catch (Exception ex){
            log.error("ERROR EDITING PRODUCT WITH NAME "+product.getName(),ex);
            return false;
        }
    }


    public boolean deleteProduct(UUID productId){
        log.info("DELETING PRODUCT WITH PRODUCT_ID "+productId);
        try{
            Optional<Product> product = productManagementRepository.findById(productId);
            if(product.isPresent()){
                productManagementRepository.deleteById(productId);
                log.info("SUCCESSFULLY DELETED PRODUCT WITH PRODUCT_ID "+productId);
                return true;
            } else {
                log.error("COULD NOT DELETE PRODUCT WITH PRODUCT_ID "+productId+", PRODUCT NOT FOUND!");
                return false;
            }
        } catch (Exception ex){
            log.error("COULD NOT DELETE PRODUCT WITH PRODUCT_ID "+productId,ex);
            return false;
        }
    }
}
