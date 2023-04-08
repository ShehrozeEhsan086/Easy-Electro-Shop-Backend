package com.easyelectroshop.productmanagementservice.Service;

import com.easyelectroshop.productmanagementservice.Model.Product;
import com.easyelectroshop.productmanagementservice.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public void saveProduct(Product product){
        productRepository.save(product);
    }

    public Optional<Product> getProductById(UUID productId){
        return productRepository.findById(productId);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
