package com.easyelectroshop.productservice.Service;

import com.easyelectroshop.productservice.DTO.AmazonS3DTO.Model3D;
import com.easyelectroshop.productservice.DTO.DiscountDTO.Discount;
import com.easyelectroshop.productservice.DTO.ProductCategoryDTO.Category;
import com.easyelectroshop.productservice.DTO.ProductCategoryDTO.SubCategory;
import com.easyelectroshop.productservice.DTO.ProductColorDTO.Color;
import com.easyelectroshop.productservice.DTO.ProductDTO.Product;
import com.easyelectroshop.productservice.DTO.ProductDTO.ProductImageWithColor;
import com.easyelectroshop.productservice.DTO.ProductDTO.ProductResponse;
import com.easyelectroshop.productservice.DTO.ProductDTO.ProductWithColor;
import com.easyelectroshop.productservice.DTO.WebScrapperDTO.WebScrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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
          Model3D model3D = webClientBuilder.build()
                  .post()
                  .uri("http://amazon-s3-service/api/v1/model/upload")
                  .contentType(MediaType.MULTIPART_FORM_DATA)
                  .body(BodyInserters.fromMultipartData("model",file.getResource()))
                  .retrieve()
                  .bodyToMono(Model3D.class)
                  .block();
          return model3D;
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

    // -----------  SERVICE FOR WEB SCRAPPER SERVICE [[START]] -------------

    public WebScrapper scrapeProductPricesAmazon(UUID productId, String productName){
        log.info("CALLING WEB SCRAPPING SERVICE TO SCRAPE PRICE FOR PRODUCT "+productName+ " ON AMAZON.COM");
        try{
            return webClientBuilder.build()
                    .post()
                    .uri("http://web-scrapping-service/api/v1/web-scrapper/scrape-product-price-amazon/"+productId+"/"+productName)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(WebScrapper.class)
                    .block();
        } catch (Exception ex){
            log.error("COULD NOT SCRAPE PRODUCT PRICE FOR PRODUCT "+productName+" [AMAZON.COM]",ex);
            return null;
        }
    }

    public WebScrapper scrapeProductPricesDaraz(UUID productId, String productName){
        log.info("CALLING WEB SCRAPPING SERVICE TO SCRAPE PRICE FOR PRODUCT "+productName+ " ON DARAZ.PK");
        try{
            return webClientBuilder.build()
                    .post()
                    .uri("http://web-scrapping-service/api/v1/web-scrapper/scrape-product-price-daraz/"+productId+"/"+productName)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(WebScrapper.class)
                    .block();
        } catch (Exception ex){
            log.error("COULD NOT SCRAPE PRODUCT PRICE FOR PRODUCT "+productName+" [DARAZ.PK]",ex);
            return null;
        }
    }

    public ResponseEntity<List<WebScrapper>> getScrappedPrices(UUID productId){
        try{
            log.info("CALLING WEB SCRAPPING SERVICE TO GET SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId);
            return webClientBuilder.build()
                    .get()
                    .uri("http://web-scrapping-service/api/v1/web-scrapper/get-scrapped-prices/"+productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(WebScrapper.class)
                    .block();
        } catch (WebClientException ex){
            log.warn("WEB SCRAPPED PRICE FOR PRODUCT WITH PRODUCT_ID "+productId+" NOT FOUND");
            return ResponseEntity.status(404).body(List.of());
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING WEB SCRAPPING SERVICE FOR PRODUCT WITH PRODUCT_ID "+productId);
            return ResponseEntity.status(500).body(List.of());
        }
    }


    public HttpStatusCode changeScrappedPriceVisibility(UUID productId){
        log.info("CALLING WEB SCRAPPING SERVICE TO CHANGE VISIBILITY OF SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            return webClientBuilder.build()
                    .put()
                    .uri("http://web-scrapping-service/api/v1/web-scrapper/change-scrapped-price-visibility/"+productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept()
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (Exception ex){
            log.error("COULD NOT CHANGE VISIBILITY OF SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode changeScrappedPriceVisibilityAmazon(UUID productId){
        log.info("CALLING WEB SCRAPPING SERVICE TO CHANGE VISIBILITY OF AMAZON SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            return webClientBuilder.build()
                    .put()
                    .uri("http://web-scrapping-service/api/v1/web-scrapper/change-scrapped-price-visibility-amazon/"+productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept()
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (Exception ex){
            log.error("COULD NOT CHANGE VISIBILITY OF AMAZON SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode changeScrappedPriceVisibilityDaraz(UUID productId){
        log.info("CALLING WEB SCRAPPING SERVICE TO CHANGE VISIBILITY OF DARAZ SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            return webClientBuilder.build()
                    .put()
                    .uri("http://web-scrapping-service/api/v1/web-scrapper/change-scrapped-price-visibility-daraz/"+productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept()
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (Exception ex){
            log.error("COULD NOT CHANGE VISIBILITY OF DARAZ SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }


    // ------------  SERVICE FOR WEB SCRAPPER SERVICE [[END]] --------------

    // -----------  SERVICE FOR PRODUCT MANAGEMENT SERVICE [[START]] -------------

    public List<Product> findTopFiveByName(String productName){
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO RETRIEVE TOP SEARCH RESULT WITH PRODUCT_NAME "+productName);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-top-search-results/"+productName)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(Product.class)
                    .block()
                    .getBody();
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING PRODUCT MANAGEMENT SERVICE TO RETRIEVE TOP SEARCH RESULT WITH PRODUCT_NAME "+productName,ex);
            return null;
        }
    }

    public List<Product> findByName(String productName,int pageNumber,int pageSize,String sortBy){
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO RETRIEVE SEARCH RESULT WITH PRODUCT_NAME "+productName);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-search-results/"+productName+"?pageNumber="+pageNumber+"&pageSize="+pageSize+"&sort="+sortBy)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(Product.class)
                    .block()
                    .getBody();
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING PRODUCT MANAGEMENT SERVICE TO RETRIEVE SEARCH RESULT WITH PRODUCT_NAME "+productName,ex);
            return null;
        }
    }

    public HttpStatusCode saveProduct(Product product) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO ADD PRODUCT WITH PRODUCT_NAME "+product.name());
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

    public List<ProductResponse> getAllProducts(int pageNumber, int pageSize, String sortBy) {
        try {
            log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET ALL PRODUCTS");
            List<Product> baseProducts = webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-all?pageNumber=" + pageNumber + "&pageSize=" + pageSize + "&sort=" + sortBy)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(Product.class)
                    .block()
                    .getBody();

            List<ProductResponse> productResponseList = new ArrayList<>();

            for (int i = 0; i < baseProducts.size(); i++) {

                try {
                    Discount discount = webClientBuilder.build()
                            .get()
                            .uri("http://discount-service/api/v1/discount/get-active-by-product-id/" + baseProducts.get(i).productId())
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .toEntity(Discount.class)
                            .block()
                            .getBody();

                    double discountedPrice = baseProducts.get(i).price() - ( baseProducts.get(i).price() * ((double) discount.discountPercentage() / 100));

                    ProductResponse productResponse = new ProductResponse(baseProducts.get(i).productId()
                            , baseProducts.get(i).name(), baseProducts.get(i).images(), baseProducts.get(i).shortDescription()
                            , baseProducts.get(i).completeDescription(), baseProducts.get(i).coverImage(), baseProducts.get(i).brandName()
                            , baseProducts.get(i).price(), true, discount.discountPercentage(), discountedPrice, baseProducts.get(i).quantity()
                            , baseProducts.get(i).size(), baseProducts.get(i).colors(), baseProducts.get(i).category(), baseProducts.get(i).subCategories()
                            , baseProducts.get(i)._3DModelFilename(), baseProducts.get(i)._3DModelURL(), baseProducts.get(i).available(), baseProducts.get(i).lastUpdated()
                            , baseProducts.get(i).scrappedPrices());

                    productResponseList.add(productResponse);

                } catch (WebClientResponseException.NotFound notFound) {
                    ProductResponse productResponse = new ProductResponse(baseProducts.get(i).productId()
                            , baseProducts.get(i).name(), baseProducts.get(i).images(), baseProducts.get(i).shortDescription()
                            , baseProducts.get(i).completeDescription(), baseProducts.get(i).coverImage(), baseProducts.get(i).brandName()
                            , baseProducts.get(i).price(), false, 0, 0, baseProducts.get(i).quantity()
                            , baseProducts.get(i).size(), baseProducts.get(i).colors(), baseProducts.get(i).category(), baseProducts.get(i).subCategories()
                            , baseProducts.get(i)._3DModelFilename(), baseProducts.get(i)._3DModelURL(), baseProducts.get(i).available(), baseProducts.get(i).lastUpdated()
                            , baseProducts.get(i).scrappedPrices());

                    productResponseList.add(productResponse);
                }

            }

            return productResponseList;

        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }

    }

    public Double getPriceByProductId(UUID productId){
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET PRICE OF PRODUCT WITH PRODUCT_ID "+productId);
        try{
           return webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-price-by-id/"+productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Double.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING PRODUCT MANAGEMENT SERVICE TO GET PRICE OF PRODUCT WITH PRODUCT_ID "+productId ,ex);
            return -1.0;
        }
    }

    public Integer getProductStock(UUID productId){
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET STOCK OF PRODUCT WITH PRODUCT_ID "+productId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-stock-by-id/"+productId)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR PRODUCT MANAGEMENT SERVICE TO GET STOCK OF PRODUCT WITH PRODUCT_ID "+productId,ex);
            return -1;
        }
    }

    public HttpStatusCode reduceStock(UUID productId, int quantity){
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO REDUCE STOCK BY QUANTITY "+quantity+" FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            return webClientBuilder.build()
                    .put()
                    .uri("http://product-management-service/api/v1/product-management/reduce-product-stock/"+productId+"/"+quantity)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (WebClientResponseException.NotAcceptable notAcceptable){
            log.error("CANNOT REDUCE STOCK FOR PRODUCT WITH PRODUCT_ID "+productId+" NOT ENOUGH STOCK TO FULL FILL REQUEST");
            return HttpStatusCode.valueOf(406);
        } catch (WebClientResponseException.NotFound notFound){
            log.error("CANNOT REDUCE STOCK FOR PRODUCT WITH PRODUCT_ID "+productId+" NOT FOUND");
            return HttpStatusCode.valueOf(404);
        } catch (Exception ex){
            log.error("ERROR CALLING MANAGEMENT SERVICE TO REDUCE STOCK BY QUANTITY "+quantity+" FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode increaseStock(UUID productId, int quantity){
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO INCREASE STOCK BY QUANTITY "+quantity+" FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            return webClientBuilder.build()
                    .put()
                    .uri("http://product-management-service/api/v1/product-management/increase-product-stock/"+productId+"/"+quantity)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("CANNOT INCREASE STOCK FOR PRODUCT WITH PRODUCT_ID "+productId+" NOT FOUND");
            return HttpStatusCode.valueOf(404);
        } catch (Exception ex){
            log.error("ERROR CALLING MANAGEMENT SERVICE TO INCREASE STOCK BY QUANTITY "+quantity+" FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }


    public ProductWithColor getProductByIdWithColorValue(UUID productId) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT WITH PRODUCT_ID "+productId);
        try{
            Product product = webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-product/"+productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .block();
            List<WebScrapper> webScrapper = getScrappedPrices(productId).getBody();
            List<String> colorValues = new ArrayList<>();
            List<ProductImageWithColor> productImageWithColors = new ArrayList<>();

            for(int i=0;i<product.images().size();i++){
                String colorValue = getColor(product.images().get(i).colors()).colorName();
                ProductImageWithColor imageWithColor = new ProductImageWithColor(product.images().get(i).imageId(),
                        product.images().get(i).imageData(),colorValue);
                productImageWithColors.add(imageWithColor);
            }

            for(int i=0;i<product.colors().size();i++){
                colorValues.add(getColor(product.colors().get(i)).colorName());
            }

            Discount discount;

            try{
                discount = webClientBuilder.build()
                        .get()
                        .uri("http://discount-service/api/v1/discount/get-active-by-product-id/" + productId)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntity(Discount.class)
                        .block()
                        .getBody();
            } catch (WebClientResponseException.NotFound notFound){
                discount = null;
            } catch (Exception ex){
                log.error("ERROR CALLING DISCOUNT SERVER, FALLING BACK TO DEFAULT VALUES");
                discount = null;
            }

            ProductWithColor completeProduct;

            if(discount!=null){
                double discountedPrice = product.price() - ( product.price() * ((double) discount.discountPercentage() / 100));

                completeProduct = new ProductWithColor(product.productId(),product.name(),productImageWithColors,product.shortDescription(),product.completeDescription(),product.coverImage(),
                        product.brandName(),product.price(),true,discount.discountPercentage(),discountedPrice,product.quantity(),product.size(),colorValues,
                        product.category(),product.subCategories(),product._3DModelFilename(),product._3DModelURL(),product.available(),product.lastUpdated(),webScrapper);

            } else {
                completeProduct = new ProductWithColor(product.productId(),product.name(),productImageWithColors,product.shortDescription(),product.completeDescription(),product.coverImage(),
                        product.brandName(),product.price(),false,0,0,product.quantity(),product.size(),colorValues,
                        product.category(),product.subCategories(),product._3DModelFilename(),product._3DModelURL(),product.available(),product.lastUpdated(),webScrapper);

            }

            return completeProduct;
        } catch (WebClientException ex){
            log.error("PRODUCT WITH PRODUCT_ID "+productId+" NOT FOUND");
            return null;
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE PRODUCT WITH PRODUCT_ID "+productId,ex);
            return null;
        }
    }

    public Product getProductById(UUID productId) {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET PRODUCT WITH PRODUCT_ID "+productId);
        try{
            Product product = webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-product/"+productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .block();
            List<WebScrapper> webScrapper = getScrappedPrices(productId).getBody();
            Product completeProduct = new Product(product.productId(),product.name(),product.images(),product.shortDescription(),product.completeDescription(),product.coverImage(),
                    product.brandName(),product.price(),product.quantity(),product.size(),product.colors(),
                    product.category(),product.subCategories(),product._3DModelFilename(),product._3DModelURL(),product.available(),product.lastUpdated(),webScrapper);
            return completeProduct;
        } catch (WebClientException ex){
            log.error("PRODUCT WITH PRODUCT_ID "+productId+" NOT FOUND");
            return null;
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
        } catch (WebClientException ex){
            log.error("CATEGORY WITH CATEGORY_ID "+categoryId+" NOT FOUND",ex);
            return null;
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING CATEGORY SERVICE TO GET CATEGORY WITH CATEGORY_ID "+categoryId,ex);
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
        } catch (WebClientException ex){
            log.error("SUB-CATEGORIES FOR CATEGORY_ID "+categoryId+" NOT FOUND",ex);
            return null;
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
        } catch (WebClientException ex){
            log.error("COLOR WITH COLOR_ID "+colorId+" NOT FOUND",ex);
            return null;
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