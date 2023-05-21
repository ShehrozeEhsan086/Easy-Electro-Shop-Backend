package com.easyelectroshop.productservice.Service;

import com.easyelectroshop.productservice.DTO.AmazonS3DTO.Model3D;
import com.easyelectroshop.productservice.DTO.ProductCategoryDTO.Category;
import com.easyelectroshop.productservice.DTO.ProductCategoryDTO.SubCategory;
import com.easyelectroshop.productservice.DTO.ProductColorDTO.Color;
import com.easyelectroshop.productservice.DTO.ProductDTO.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductService {

    @Autowired
    WebClient.Builder webClientBuilder;

    @Autowired
    MultipartBodyBuilder multipartBodyBuilder;

    // ----------------  SERVICE FOR AMAZON SERVICE [[START]] --------------------

    public Model3D uploadModel(MultipartFile file) {
      log.info("CALLING AMAZON S3 SERVICE TO UPLOAD MODEL FILE WITH FILE_NAME  "+file.getOriginalFilename());
      try{
          multipartBodyBuilder.part("model",file.getResource());
          var payLoad = multipartBodyBuilder.build();
          return webClientBuilder.build()
                  .post()
                  .uri("http://amazon-s3-service/api/v1/model/upload")
                  .contentType(MediaType.MULTIPART_FORM_DATA)
                  .body(BodyInserters.fromMultipartData(payLoad))
                  .retrieve()
                  .bodyToMono(Model3D.class)
                  .block();
      } catch (Exception ex){
          log.error("ERROR WHILE CALLING AMAZON S3 SERVICE FOR UPLOADING MODEL FILE WITH FILE_NAME "+file.getOriginalFilename(),ex);
          return null;
      }
    }

    public HttpStatusCode deleteModel(String fileName){
        log.info("CALLING AMAZON S3 SERVICE TO DELETE MODEL FILE WITH FILE_NAME "+fileName);
        try{
            return webClientBuilder.build()
                    .delete()
                    .uri("http://amazon-s3-service/api/v1/model//delete/"+fileName)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (Exception ex){
            log.error("COULD NOT FIND MODEL FILE WITH FILE_NAME "+fileName,ex);
            return HttpStatusCode.valueOf(404);
        }
    }

    // -----------------  SERVICE FOR AMAZON SERVICE [[END]] ---------------------


    // -----------  SERVICE FOR PRODUCT MANAGEMENT SERVICE [[START]] -------------

    public HttpStatusCode saveProduct(Product product) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO ADD WITH PRODUCT_NAME "+product.name());
        try{
            return webClientBuilder.build()
                    .post()
                    .uri("http://product-management-service/api/v1/product-management/add-product")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(product))
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING PRODUCT MANAGEMENT SERVICE TO ADD PRODUCT WITH PRODUCT_NAME "+product.name(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public List<Product> getAllProducts(int pageNumber,int pageSize,String sortBy) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET ALL PRODUCTS");
        try{
             return webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-all?pageNumber="+pageNumber+"&pageSize="+pageSize+"&sort="+sortBy)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(Product.class)
                    .block()
                     .getBody();
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE ALL PRODUCTS",ex);
            return null;
        }
    }

    public Product getProductById(UUID productId) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT WITH PRODUCT_ID "+productId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-product/"+productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .block();
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE PRODUCT WITH PRODUCT_ID "+productId,ex);
            return null;
        }
    }

    public HttpStatusCode updateProduct(Product product) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO UPDATE PRODUCT WITH PRODUCT_NAME "+product.name());
        try{
            Product tempProduct = getProductById(product.productId());
            if (tempProduct != null){
                return webClientBuilder.build()
                        .put()
                        .uri("http://product-management-service/api/v1/product-management/update-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(product))
                        .retrieve()
                        .toBodilessEntity()
                        .flatMap(response -> Mono.just(response.getStatusCode()))
                        .block();
            } else {
                log.info("COULD NOT FIND PRODUCT WITH PRODUCT_NAME "+product.name()+", ADDING NEW PRODUCT");
                return saveProduct(product);
            }
        } catch (Exception ex){
            log.error("COULD NOT UPDATE PRODUCT WITH PRODUCT_NAME "+product.name(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode deleteProduct(UUID productId) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO DELETE PRODUCT WITH PRODUCT_ID "+productId);
        try{
            Product tempProduct = getProductById(productId);
            if (tempProduct != null){
                return webClientBuilder.build()
                        .delete()
                        .uri("http://product-management-service/api/v1/product-management/delete-product/"+productId)
                        .retrieve()
                        .toBodilessEntity()
                        .flatMap(response -> Mono.just(response.getStatusCode()))
                        .block();
            } else {
                log.error("COULD NOT FIND THE PRODUCT WITH PRODUCT_ID "+productId);
                return HttpStatusCode.valueOf(404);
            }
        } catch (Exception ex){
            log.error("COULD NOT DELETE PRODUCT WITH PRODUCT_ID "+productId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public Integer getProductsCount(){
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET NUMBERS OF PRODUCTS");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-all-count")
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING PRODUCT MANAGEMENT SERVICE TO GET NUMBERS OF PRODUCTS",ex);
            return 0;
        }
    }

    // -------------  SERVICE FOR PRODUCT MANAGEMENT SERVICE [[END]] ---------------

    // -------------  SERVICE FOR PRODUCT CATEGORY SERVICE [[START]] ---------------

    public HttpStatusCode saveCategory(Category category) {
        log.info("CALLING CATEGORY SERVICE TO ADD NEW CATEGORY WITH CATEGORY_NAME "+category.categoryName());
        try{
            return webClientBuilder.build()
                    .post()
                    .uri("http://product-category-management-service/api/v1/category/add-category")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(category))
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING CATEGORY MANAGEMENT SERVICE TO ADD NEW CATEGORY WITH CATEGORY_NAME "+category.categoryName(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public List<Category> getAllCategories() {
        log.info("CALLING CATEGORY SERVICE TO GET ALL CATEGORIES");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://product-category-management-service/api/v1/category/get-all")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(Category.class)
                    .block()
                    .getBody();
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING CATEGORY SERVICE TO GET ALL CATEGORIES",ex);
            return null;
        }
    }

    public Category getCategory(long categoryId){
        log.info("CALLING CATEGORY SERVICE TO GET CATEGORY WITH CATEGORY_ID "+categoryId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://product-category-management-service/api/v1/category/get-category/"+categoryId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(Category.class)
                    .block()
                    .getBody();
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING CATEGORY SERVICE TO GET CATEGORY WITH CATEGORY_ID "+categoryId+" POSSIBLY NOT FOUND!",ex);
            return null;
        }
    }

    public HttpStatusCode updateCategory(Category category) {
        log.info("CALLING CATEGORY SERVICE TO EDIT CATEGORY WITH CATEGORY_NAME "+category.categoryName());
        try{
            Category tempCategory = getCategory(category.categoryId());
            if( tempCategory != null){
                return webClientBuilder.build()
                        .put()
                        .uri("http://product-category-management-service/api/v1/category/edit-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(category))
                        .retrieve()
                        .toBodilessEntity()
                        .flatMap(response -> Mono.just(response.getStatusCode()))
                        .block();
            } else {
                log.error("COULD NOT FIND CATEGORY WITH CATEGORY_NAME "+category.categoryName()+ " ADDING NEW CATEGORY");
                return saveCategory(category);
            }
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING CATEGORY SERVICE TO UPDATE CATEGORY WITH CATEGORY_NAME "+category.categoryName(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode deleteCategory(long categoryId) {
        log.info("CALLING CATEGORY SERVICE TO DELETE CATEGORY WITH CATEGORY_ID "+categoryId);
        try{
            Category tempCategory = getCategory(categoryId);
            if (tempCategory != null){
                return webClientBuilder.build()
                        .delete()
                        .uri("http://product-category-management-service/api/v1/category/delete-category/"+categoryId)
                        .retrieve()
                        .toBodilessEntity()
                        .flatMap(response -> Mono.just(response.getStatusCode()))
                        .block();
            } else {
                log.error("COULD NOT FIND THE CATEGORY WITH CATEGORY_ID "+categoryId);
                return HttpStatusCode.valueOf(404);
            }
        } catch (Exception ex){
            log.error("COULD NOT DELETE CATEGORY WITH CATEGORY_ID "+categoryId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public List<SubCategory> getSubCategories(long categoryId) {
        log.info("CALLING CATEGORY SERVICE TO GET SUB-CATEGORIES FOR CATEGORY WITH CATEGORY_ID "+categoryId);
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://product-category-management-service/api/v1/category/get-subcategories/"+categoryId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(SubCategory.class)
                    .block()
                    .getBody();
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING CATEGORY SERVICE TO GET SUB-CATEGORIES FOR CATEGORY WITH CATEGORY_ID "+categoryId,ex);
            return null;
        }
    }

    // --------------  SERVICE FOR PRODUCT CATEGORY SERVICE [[END]] ----------------

    // --------------  SERVICE FOR PRODUCT COLOR SERVICE [[START]] -----------------


    public HttpStatusCode saveColor(Color color){
        log.info("CALLING COLOR SERVICE TO ADD NEW COLOR WITH COLOR_NAME "+color.colorName());
        try{
            return webClientBuilder.build()
                    .post()
                    .uri("http://product-color-management-service/api/v1/color/add-color")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(color))
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING COLOR MANAGEMENT SERVICE TO ADD NEW COLOR WITH COLOR_NAME "+color.colorName(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public List<Color> getAllColors() {
        log.info("CALLING COLOR SERVICE TO GET ALL COLORS");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://product-color-management-service/api/v1/color/get-all")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(Color.class)
                    .block()
                    .getBody();
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING COLOR SERVICE TO GET ALL COLORS",ex);
            return null;
        }
    }

    public Color getColor(long colorId) {
        log.info("CALLING COLOR SERVICE TO GET COLOR WITH COLOR_ID "+colorId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://product-color-management-service/api/v1/color/get-color/"+colorId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(Color.class)
                    .block()
                    .getBody();
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING COLOR SERVICE TO GET COLOR WITH COLOR_ID "+colorId+" POSSIBLY NOT FOUND!",ex);
            return null;
        }
    }

    public HttpStatusCode updateColor(Color color) {
        log.info("CALLING COLOR SERVICE TO EDIT COLOR WITH COLOR_NAME "+color.colorName());
        try{
            Color tempColor = getColor(color.colorId());
            if( tempColor != null){
                return webClientBuilder.build()
                        .put()
                        .uri("http://product-color-management-service/api/v1/color/update-color")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(color))
                        .retrieve()
                        .toBodilessEntity()
                        .flatMap(response -> Mono.just(response.getStatusCode()))
                        .block();
            } else {
                log.error("COULD NOT FIND COLOR WITH COLOR_NAME "+color.colorName()+ " ADDING NEW COLOR");
                return saveColor(color);
            }
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING COLOR SERVICE TO UPDATE COLOR WITH COLOR_NAME "+color.colorName(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }


    public HttpStatusCode deleteColor(long colorId) {
        log.info("CALLING COLOR SERVICE TO DELETE COLOR WITH COLOR_ID "+colorId);
        try{
            Color tempColor = getColor(colorId);
            if (tempColor != null){
                return webClientBuilder.build()
                        .delete()
                        .uri("http://product-color-management-service/api/v1/color/delete-color/"+colorId)
                        .retrieve()
                        .toBodilessEntity()
                        .flatMap(response -> Mono.just(response.getStatusCode()))
                        .block();
            } else {
                log.error("COULD NOT FIND THE COLOR WITH COLOR_ID "+colorId);
                return HttpStatusCode.valueOf(404);
            }
        } catch (Exception ex){
            log.error("COULD NOT DELETE COLOR WITH COLOR_ID "+colorId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    // ---------------  SERVICE FOR PRODUCT COLOR SERVICE [[END]] ------------------
}