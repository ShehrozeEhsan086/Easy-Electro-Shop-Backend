package com.easyelectroshop.productmanagementservice.Service;

import com.easyelectroshop.productmanagementservice.Model.Product;
import com.easyelectroshop.productmanagementservice.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(rollbackOn = Exception.class)
public class ProductService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    Date date;
    @Autowired
    SimpleDateFormat simpleDateFormat;


    public void saveProduct(Product product){
        product.setLastUpdated(simpleDateFormat.format(date.getTime()));
        productRepository.save(product);
    }

    public Optional<Product> getProductById(UUID productId){
        return productRepository.findById(productId);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> updateProduct(Product product) {
        productRepository.save(product);
        return productRepository.findById(product.getProductId());
    }
}
