package com.easyelectroshop.productmanagementservice.Service;

import com.easyelectroshop.productmanagementservice.Model.Product;
import com.easyelectroshop.productmanagementservice.Repository.ProductManagementRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(rollbackOn = Exception.class)
public class ProductManagementService {

    @Autowired
    ProductManagementRepository productManagementRepository;
    @Autowired
    Date date;
    @Autowired
    SimpleDateFormat simpleDateFormat;


    public void saveProduct(Product product){
        product.setLastUpdated(simpleDateFormat.format(date.getTime()));
        productManagementRepository.save(product);
    }

    public Optional<Product> getProductById(UUID productId){
        return productManagementRepository.findById(productId);
    }

    public List<Product> getAllProducts() {
        return productManagementRepository.findAll();
    }

    public void updateProduct(Product product) {
        productManagementRepository.save(product);
    }
}
