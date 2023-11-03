package com.easyelectroshop.productmanagementservice.Service;

import com.easyelectroshop.productmanagementservice.DTO.ProductNameImagePrice.ProductMinimalData;
import com.easyelectroshop.productmanagementservice.DTO.ProductPriceQuantity.ProductPriceQuantity;
import com.easyelectroshop.productmanagementservice.DTO.ProductWithoutImages.ProductWithoutImages;
import com.easyelectroshop.productmanagementservice.Model.Product;
import com.easyelectroshop.productmanagementservice.Repository.ProductManagementRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class ProductManagementService {

    @Autowired
    ProductManagementRepository productManagementRepository;

    @Autowired
    Date date;

    @Autowired
    SimpleDateFormat simpleDateFormat;

    @Autowired
    DecimalFormat decimalFormat;

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

    public List<ProductWithoutImages> getAllProducts(int pageNumber, int pageSize, String sortBy) {
        log.info("GETTING ALL PRODUCTS");
        try{
            if(pageSize == -1){
                pageSize = (int) productManagementRepository.count();
            }
            Page<Product> products = productManagementRepository.findAll(PageRequest.of(pageNumber,pageSize, Sort.by(sortBy).descending()));
            List<ProductWithoutImages> productWithoutImages = new ArrayList<>();
            for(Product product : products){
                ProductWithoutImages productWithoutImage = new ProductWithoutImages(product.getProductId(),product.getName(),product.getShortDescription(),product.getCompleteDescription(),product.getCoverImage(),
                        product.getBrandName(),product.getPrice(),product.getQuantity(),
                        product.getSize(),product.getColors(), product.getCategory(),product.getSubCategories(),product.get_3DModelFilename(),product.get_3DModelURL(),product.isAvailable(),
                        product.getLastUpdated());
                productWithoutImages.add(productWithoutImage);
            }
            log.info("SUCCESSFULLY RETRIEVED ALL PRODUCTS");
            return productWithoutImages;
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE ALL PRODUCTS",ex);
            return null;
        }
    }

    public List<ProductWithoutImages> getAllByCategory(long categoryId,int pageNumber, int pageSize, String sortBy){
        log.info("GETTING ALL PRODUCTS WITH CATEGORY OF "+categoryId);
        try{
            if(pageSize == -1){
                pageSize = (int) productManagementRepository.count();
            }
            Page<Product> products = productManagementRepository.findAllByCategory(categoryId,PageRequest.of(pageNumber,pageSize, Sort.by(sortBy).descending()));
            List<ProductWithoutImages> productWithoutImages = new ArrayList<>();
            for(Product product : products){
                ProductWithoutImages productWithoutImage = new ProductWithoutImages(product.getProductId(),product.getName(),product.getShortDescription(),product.getCompleteDescription(),product.getCoverImage(),
                        product.getBrandName(),product.getPrice(),product.getQuantity(),
                        product.getSize(),product.getColors(), product.getCategory(),product.getSubCategories(),product.get_3DModelFilename(),product.get_3DModelURL(),product.isAvailable(),
                        product.getLastUpdated());
                productWithoutImages.add(productWithoutImage);
            }
            log.info("SUCCESSFULLY RETRIEVED ALL PRODUCTS");
            return productWithoutImages;
        } catch (Exception ex){
            log.error("ERROR GETTING ALL PRODUCTS WITH CATEGORY OF "+categoryId);
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
            log.error("COULD NOT RETRIEVE PRODUCTS COUNT",ex);
            return -1;
        }
    }

    public HttpStatusCode updateProduct(Product product) {
        log.info("UPDATING PRODUCT WITH NAME "+product.getName());
        try{
            Optional<Product> tempProduct = productManagementRepository.findById(product.getProductId());
            if(tempProduct.isPresent()){
                productManagementRepository.save(product);
                log.info("SUCCESSFULLY EDITED PRODUCT WITH NAME "+product.getName());
                return HttpStatusCode.valueOf(200);
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

    public ResponseEntity<List<Product>> findTopFiveByName(String name){
        log.info("GETTING TOP FIVES PRODUCTS SEARCHED BY NAME");
        try{
            List<Product> products = productManagementRepository.findTopFiveByName(name);
            log.info("SUCCESSFULLY RETRIEVED TOP FIVES PRODUCTS SEARCHED BY NAME");
            return ResponseEntity.ok(products);
        } catch (Exception ex){
            log.error("ERROR RETRIEVING TOP FIVES PRODUCTS SEARCHED BY NAME ", ex);
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }
    }

    public ResponseEntity<List<Product>> findByName(String name,int pageNumber,int pageSize,String sortBy){
        log.info("GETTING PRODUCTS SEARCHED BY NAME");
        try{
            List<Product> products = productManagementRepository.findByName(name,sortBy,pageSize,pageNumber);
            log.info("SUCCESSFULLY RETRIEVED PRODUCTS SEARCHED BY NAME");
            return ResponseEntity.ok(products);
        } catch (Exception ex){
            log.error("ERROR RETRIEVING PRODUCTS SEARCHED BY NAME ", ex);
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }
    }

    public ResponseEntity<Integer> getProductStock(UUID productId){
        log.info("GETTING PRODUCT STOCK REMAINING FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            int stock = productManagementRepository.findStockByProductId(productId);
            return ResponseEntity.ok(stock);
        }catch (Exception ex){
            log.error("ERROR GETTING PRODUCT STOCK REMAINING FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> reduceStock(UUID productId,int quantity){
        log.info("REDUCING STOCK BY QUANTITY OF "+quantity+" FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            Optional<Product> product = productManagementRepository.findById(productId);
            if(product.isPresent()){
                int currentStock = product.get().getQuantity();
                if(currentStock - quantity < 0 ){
                    log.error("CANNOT REDUCE PRODUCT QUANTITY FOR PRODUCT WITH PRODUCT_ID "+product+" NOT ENOUGH STOCK TO FULL FILL REQUEST.");
                    return ResponseEntity.status(HttpStatusCode.valueOf(406)).build();
                } else {
                    currentStock = currentStock - quantity;
                    product.get().setQuantity(currentStock);
                    productManagementRepository.save(product.get());
                    return ResponseEntity.ok().build();
                }
            } else {
                log.error("CANNOT REDUCE PRODUCT QUANTITY FOR PRODUCT WITH PRODUCT_ID "+productId+" NOT FOUND.");
                return ResponseEntity.notFound().build();
            }
        } catch  (Exception ex){
            log.error("ERROR REDUCING STOCK BY QUANTITY OF "+quantity+" FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> increaseStock(UUID productId,int quantity){
        log.info("INCREASING STOCK BY QUANTITY OF "+quantity+" FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            Optional<Product> product = productManagementRepository.findById(productId);
            if(product.isPresent()){
                int currentStock = product.get().getQuantity();
                currentStock = currentStock + quantity;
                product.get().setQuantity(currentStock);
                productManagementRepository.save(product.get());
                return ResponseEntity.ok().build();
            } else {
                log.error("CANNOT INCREASE PRODUCT QUANTITY FOR PRODUCT WITH PRODUCT_ID "+productId+" NOT FOUND.");
                return ResponseEntity.notFound().build();
            }
        } catch  (Exception ex){
            log.error("ERROR INCREASING STOCK BY QUANTITY OF "+quantity+" FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<String> getProductNameById(UUID productId){
        log.info("GETTING PRODUCT NAME FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            String productName = productManagementRepository.findById(productId).get().getName();
            log.info("SUCCESSFULLY RETRIEVED PRODUCT NAME "+productName+" FOR PRODUCT WITH PRODUCT_ID "+productId);
            return ResponseEntity.ok(productName);
        } catch (NoSuchElementException noSuchElementException){
            log.error("ERROR GETTING PRODUCT NAME FOR PRODUCT WITH PRODUCT_ID "+productId+" FIELD NOT FOUND!");
            return ResponseEntity.notFound().build();
        }catch (Exception ex){
            log.error("ERROR GETTING PRODUCT NAME FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<String> getTotalInventoryPrice(){
        log.info("GETTING TOTAL INVENTORY PRICE");
        try{
            double totalInventoryPrice = 0;
            List<ProductPriceQuantity> priceAndQuantityValues = productManagementRepository.getAllPriceQuantity();
            if (priceAndQuantityValues.isEmpty()){
                log.info("PRODUCT PRICE AND QUANTITY VALUES RETURNED EMPTY");
                return ResponseEntity.ok("0.0");
            } else {
                for(int i=0;i<priceAndQuantityValues.size();i++){
                    log.info("PRODUCT "+(i+1)+" PRICE "+priceAndQuantityValues.get(i).price()+ " QUANTITY "+priceAndQuantityValues.get(i).quantity());
                    totalInventoryPrice = totalInventoryPrice + (priceAndQuantityValues.get(i).quantity() * priceAndQuantityValues.get(i).price());
                }
                log.info("RECEIVED INFO FOR TOTAL PRODUCTS COUNT "+priceAndQuantityValues.size()+" TOTAL INVENTORY PRICE "+totalInventoryPrice);
                String formattedInventoryPrice = decimalFormat.format(totalInventoryPrice);
                return ResponseEntity.ok(formattedInventoryPrice);
            }
        } catch (Exception ex){
            log.error("ERROR GETTING TOTAL INVENTORY PRICE ",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getCategoryByProductId(UUID productId){
        log.info("GETTING CATEGORY OF PRODUCT WITH PRODUCT_ID "+productId);
        try{
            Optional<Product> product = productManagementRepository.findById(productId);
            if(product.isPresent()){
                return ResponseEntity.ok(product.get().getCategory());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<ProductMinimalData> getProductNameImagePriceById(UUID productId){
        log.info("GETTING PRODUCT NAME, PRICE AND COVER IMAGE FOR PRODUCT_ID "+productId);
        try{
            Optional<Product> product = productManagementRepository.findById(productId);
            if (product.isPresent()){
                ProductMinimalData productResponse = new ProductMinimalData(productId,product.get().getName(),product.get().getCoverImage());
                return ResponseEntity.ok(productResponse);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

}
