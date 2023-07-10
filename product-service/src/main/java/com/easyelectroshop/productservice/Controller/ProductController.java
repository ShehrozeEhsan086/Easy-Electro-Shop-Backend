package com.easyelectroshop.productservice.Controller;

import com.easyelectroshop.productservice.DTO.AmazonS3DTO.Model3D;
import com.easyelectroshop.productservice.DTO.ProductCategoryDTO.Category;
import com.easyelectroshop.productservice.DTO.ProductCategoryDTO.SubCategory;
import com.easyelectroshop.productservice.DTO.ProductColorDTO.Color;
import com.easyelectroshop.productservice.DTO.ProductDTO.Product;
import com.easyelectroshop.productservice.DTO.WebScrapperDTO.WebScrapper;
import com.easyelectroshop.productservice.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@CrossOrigin //change later
@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    @Autowired
    ProductService productService;

    // ----------------  APIS FOR AMAZON SERVICE [[START]] --------------------

    @PostMapping("/upload-model")
    public ResponseEntity<Model3D> uploadModel(@RequestParam(value = "model") MultipartFile file){
        System.out.println("Hit");
        Model3D uploadedContent = productService.uploadModel(file);
        return(uploadedContent != null) ? ResponseEntity.ok(uploadedContent) : ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("/delete-model/{fileName}")
    public ResponseEntity<HttpStatusCode> deleteModel(@PathVariable String fileName){
        return ResponseEntity.status(productService.deleteModel(fileName)).build();
    }

    //DOWNLOAD FILE NOT IMPLEMENTED

    // -----------------  APIS FOR AMAZON SERVICE [[END]] ----------------------

    // --------------  APIS FOR WEB-SCRAPPING SERVICE [[START]] ----------------

    @PostMapping("/scrape-product-prices-amazon/{productId}/{productName}")
    public ResponseEntity<WebScrapper> scrapeProductPricesAmazon(@PathVariable UUID productId , @PathVariable String productName){
        WebScrapper response = productService.scrapeProductPricesAmazon(productId,productName);
        return ( response != null) ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @PostMapping("/scrape-product-prices-daraz/{productId}/{productName}")
    public ResponseEntity<WebScrapper> scrapeProductPricesDaraz(@PathVariable UUID productId , @PathVariable String productName){
        WebScrapper response = productService.scrapeProductPricesDaraz(productId,productName);
        return ( response != null) ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/get-scrapped-prices/{productId}")
    public ResponseEntity<List<WebScrapper>> getScrappedPrices(@PathVariable UUID productId){
        return productService.getScrappedPrices(productId);
    }

    @PutMapping("/change-scrapped-price-visibility/{productId}")
    public ResponseEntity<HttpStatusCode> changeScrappedPricesVisibility(@PathVariable UUID productId){
        HttpStatusCode statusCode = productService.changeScrappedPriceVisibility(productId);
        return ResponseEntity.status(statusCode).build();
    }

    // ---------------  APIS FOR WEB-SCRAPPING SERVICE [[END]] -----------------

    // -----------  APIS FOR PRODUCT MANAGEMENT SERVICE [[START]] -------------

    @PostMapping("/add-product")
    public ResponseEntity<HttpStatusCode> saveProduct(@RequestBody Product product){
        return ResponseEntity.status(productService.saveProduct(product)).build();
    }

    @GetMapping("/get-all-products")
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
                                                        @RequestParam(value="pageSize",defaultValue = "5",required = false) int pageSize,
                                                        @RequestParam(value="sort",defaultValue = "lastUpdated",required = false) String sortBy){
        List<Product> products = productService.getAllProducts(pageNumber,pageSize,sortBy);
        return(products!=null) ? ResponseEntity.ok(products) : ResponseEntity.unprocessableEntity().build() ;
    }

    @GetMapping("get-all-products-count")
    public ResponseEntity<Integer> getProductsCount(){
        int productCount = productService.getProductsCount();
        return (productCount!=0) ? ResponseEntity.ok(productCount) : ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("get-product/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID productId){
        Product product = productService.getProductById(productId);
        return(product!=null) ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @PutMapping("/update-product")
    public ResponseEntity<HttpStatusCode> updateProduct(@RequestBody Product product){
        HttpStatusCode statusCode = productService.updateProduct(product);
        return ResponseEntity.status(statusCode).build();
    }

    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<HttpStatusCode> deleteProduct(@PathVariable UUID productId){
        HttpStatusCode statusCode = productService.deleteProduct(productId);
        return ResponseEntity.status(statusCode).build();
    }

    // -------------  APIS FOR PRODUCT MANAGEMENT SERVICE [[END]] ---------------

    // -------------  APIS FOR PRODUCT CATEGORY SERVICE [[START]] ---------------

    @PostMapping("/add-category")
    public ResponseEntity<HttpStatusCode> saveCategory(@RequestBody Category category){
        return ResponseEntity.status(productService.saveCategory(category)).build();
    }

    @GetMapping("/get-all-categories")
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> categories = productService.getAllCategories();
        return (categories != null) ? ResponseEntity.ok(categories) : ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("/get-category/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable long categoryId){
        Category category = productService.getCategory(categoryId);
        return(category!=null) ? ResponseEntity.ok(category) : ResponseEntity.notFound().build();
    }

    @PutMapping("/update-category")
    public ResponseEntity<HttpStatusCode> updateCategory(@RequestBody Category category){
        return ResponseEntity.status(productService.updateCategory(category)).build();
    }

    @DeleteMapping("/delete-category/{categoryId}")
    public ResponseEntity<HttpStatusCode> deleteCategory(@PathVariable long categoryId){
        return ResponseEntity.status(productService.deleteCategory(categoryId)).build();
    }

    @GetMapping("/get-subcategories/{categoryId}")
    public ResponseEntity<List<SubCategory>> getSubCategories(@PathVariable long categoryId){
        List<SubCategory> subCategories = productService.getSubCategories(categoryId);
        return (subCategories != null) ? ResponseEntity.ok(subCategories) : ResponseEntity.notFound().build();
    }

    // --------------  APIS FOR PRODUCT CATEGORY SERVICE [[END]] ----------------

    // --------------  APIS FOR PRODUCT COLOR SERVICE [[START]] -----------------

    @PostMapping("/add-color")
    public ResponseEntity saveColor(@RequestBody Color color){
        return ResponseEntity.status(productService.saveColor(color)).build();
    }

    @GetMapping("/get-all-colors")
    public ResponseEntity<List<Color>> getAllColors(){
        List<Color> colors = productService.getAllColors();
        return (colors != null) ? ResponseEntity.ok(colors) : ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("/get-color/{colorId}")
    public ResponseEntity<Color> getColor(@PathVariable long colorId){
        Color color = productService.getColor(colorId);
        return (color != null) ? ResponseEntity.ok(color) : ResponseEntity.notFound().build();
    }

    @PutMapping("/update-color")
    public ResponseEntity<HttpStatusCode> updateColor(@RequestBody Color color){
        return ResponseEntity.status(productService.updateColor(color)).build();
    }

    @DeleteMapping("/delete-color/{colorId}")
    public ResponseEntity<HttpStatusCode> deleteColor(@PathVariable long colorId){
        return ResponseEntity.status(productService.deleteColor(colorId)).build();
    }

    // ---------------  APIS FOR PRODUCT COLOR SERVICE [[END]] -----------------

}