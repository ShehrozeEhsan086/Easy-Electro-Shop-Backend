package com.easyelectroshop.productservice.Controller;

import com.easyelectroshop.productservice.DTO.AmazonS3DTO.Model3D;
import com.easyelectroshop.productservice.DTO.DiscountDTO.Discount;
import com.easyelectroshop.productservice.DTO.DiscountDTO.DiscountResponse;
import com.easyelectroshop.productservice.DTO.ProductCategoryDTO.Category;
import com.easyelectroshop.productservice.DTO.ProductCategoryDTO.SubCategory;
import com.easyelectroshop.productservice.DTO.ProductColorDTO.Color;
import com.easyelectroshop.productservice.DTO.ProductDTO.CompleteProductResponse;
import com.easyelectroshop.productservice.DTO.ProductDTO.Product;
import com.easyelectroshop.productservice.DTO.ProductDTO.ProductResponse;
import com.easyelectroshop.productservice.DTO.ProductDTO.ProductWithColor;
import com.easyelectroshop.productservice.DTO.WebScrapperDTO.WebScrapper;
import com.easyelectroshop.productservice.Service.DiscountService;
import com.easyelectroshop.productservice.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    DiscountService discountService;

    // ----------------  APIS FOR AMAZON SERVICE [[START]] --------------------

    @PostMapping("management/upload-model")
    public ResponseEntity<Model3D> uploadModel(@RequestParam(value = "model") MultipartFile file){
        Model3D uploadedContent = productService.uploadModel(file);
        return(uploadedContent != null) ? ResponseEntity.ok(uploadedContent) : ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("management/delete-model/{fileName}")
    public ResponseEntity<HttpStatusCode> deleteModel(@PathVariable String fileName){
        return ResponseEntity.status(productService.deleteModel(fileName)).build();
    }

    //DOWNLOAD FILE NOT IMPLEMENTED

    // -----------------  APIS FOR AMAZON SERVICE [[END]] ----------------------

    // --------------  APIS FOR WEB-SCRAPPING SERVICE [[START]] ----------------

    @PostMapping("management/scrape-product-prices-amazon/{productId}/{productName}")
    public ResponseEntity<WebScrapper> scrapeProductPricesAmazon(@PathVariable UUID productId , @PathVariable String productName){
        WebScrapper response = productService.scrapeProductPricesAmazon(productId,productName);
        return ( response != null) ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @PostMapping("management/scrape-product-prices-daraz/{productId}/{productName}")
    public ResponseEntity<WebScrapper> scrapeProductPricesDaraz(@PathVariable UUID productId , @PathVariable String productName){
        WebScrapper response = productService.scrapeProductPricesDaraz(productId,productName);
        return ( response != null) ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/get-scrapped-prices/{productId}")
    public ResponseEntity<List<WebScrapper>> getScrappedPrices(@PathVariable UUID productId){
        return productService.getScrappedPrices(productId);
    }


    @PutMapping("management/change-scrapped-price-visibility/{productId}")
    public ResponseEntity<HttpStatusCode> changeScrappedPricesVisibility(@PathVariable UUID productId){
        HttpStatusCode statusCode = productService.changeScrappedPriceVisibility(productId);
        return ResponseEntity.status(statusCode).build();
    }

    @PutMapping("management/change-scrapped-price-visibility-amazon/{productId}")
    public ResponseEntity<HttpStatusCode> changeScrappedPricesVisibilityAmazon(@PathVariable UUID productId){
        HttpStatusCode statusCode = productService.changeScrappedPriceVisibilityAmazon(productId);
        return ResponseEntity.status(statusCode).build();
    }

    @PutMapping("management/change-scrapped-price-visibility-daraz/{productId}")
    public ResponseEntity<HttpStatusCode> changeScrappedPricesVisibilityDaraz(@PathVariable UUID productId){
        HttpStatusCode statusCode = productService.changeScrappedPriceVisibilityDaraz(productId);
        return ResponseEntity.status(statusCode).build();
    }

    // ---------------  APIS FOR WEB-SCRAPPING SERVICE [[END]] -----------------

    // -----------  APIS FOR PRODUCT MANAGEMENT SERVICE [[START]] -------------



    @GetMapping("/get-top-search-results/{productName}")
    public ResponseEntity<List<Product>> searchTopFiveByName(@PathVariable String productName){
        List<Product> products = productService.findTopFiveByName(productName);
        return products != null ? ResponseEntity.ok(products) : ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
    }

    @GetMapping("/get-search-results/{productName}")
    public ResponseEntity<List<Product>> searchByName(@PathVariable String productName,
                                                      @RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
                                                      @RequestParam(value="pageSize",defaultValue = "10",required = false) int pageSize,
                                                      @RequestParam(value="sort",defaultValue = "lastUpdated",required = false) String sortBy){
        List<Product> products = productService.findByName(productName,pageNumber,pageSize,sortBy);
        return products != null ? ResponseEntity.ok(products) : ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
    }

    @PostMapping("/management/add-product")
    public ResponseEntity<HttpStatusCode> saveProduct(@RequestBody Product product){
        return ResponseEntity.status(productService.saveProduct(product)).build();
    }

    @GetMapping("/get-all-products")
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value="pageSize",defaultValue = "10",required = false) int pageSize,
            @RequestParam(value="sort",defaultValue = "lastUpdated",required = false) String sortBy){
        List<ProductResponse> products = productService.getAllProducts(pageNumber,pageSize,sortBy);
        return(products!=null) ? ResponseEntity.ok(products) : ResponseEntity.unprocessableEntity().build() ;
    }

    @GetMapping("/get-all-products-by-category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @PathVariable long categoryId,
            @RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value="pageSize",defaultValue = "10",required = false) int pageSize,
            @RequestParam(value="sort",defaultValue = "lastUpdated",required = false) String sortBy){
        List<ProductResponse> products = productService.getAllProductsByCategory(categoryId,pageNumber,pageSize,sortBy);
        return(products!=null) ? ResponseEntity.ok(products) : ResponseEntity.unprocessableEntity().build() ;
    }

    @GetMapping("/get-price-by-id/{productId}")
    public ResponseEntity<Double> getPriceById(@PathVariable UUID productId){
        double price = productService.getPriceByProductId(productId);
        return price != -1.0 ? ResponseEntity.ok(price) : ResponseEntity.internalServerError().build();
    }

    @GetMapping("/get-stock-by-id/{productId}")
    public ResponseEntity<Integer> getStockById(@PathVariable UUID productId){
        int stock = productService.getProductStock(productId);
        return stock != -1 ? ResponseEntity.ok(stock) : ResponseEntity.internalServerError().build();
    }

    @PutMapping("/reduce-product-stock/{productId}/{quantity}")
    public ResponseEntity<HttpStatusCode> reduceStock(@PathVariable UUID productId,
                                                      @PathVariable int quantity){
        return ResponseEntity.status(productService.reduceStock(productId,quantity)).build();
    }

    @PutMapping("/increase-product-stock/{productId}/{quantity}")
    public ResponseEntity<HttpStatusCode> increaseStock(@PathVariable UUID productId,
                                                        @PathVariable int quantity){
        return ResponseEntity.status(productService.increaseStock(productId,quantity)).build();
    }

    @GetMapping("get-all-products-count")
    public ResponseEntity<Integer> getProductsCount(){
        int productCount = productService.getProductsCount();
        return (productCount!=0) ? ResponseEntity.ok(productCount) : ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("get-product-for-customer/{productId}")
    public ResponseEntity<ProductWithColor> getProductByIdForCustomer(@PathVariable UUID productId){
        ProductWithColor product = productService.getProductByIdWithColorValue(productId);
        return(product!=null) ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @GetMapping("get-product/{productId}")
    public ResponseEntity<CompleteProductResponse> getProductById(@PathVariable UUID productId){
        CompleteProductResponse product = productService.getProductById(productId);
        return(product!=null) ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @PutMapping("management/update-product")
    public ResponseEntity<HttpStatusCode> updateProduct(@RequestBody Product product){
        HttpStatusCode statusCode = productService.updateProduct(product);
        return ResponseEntity.status(statusCode).build();
    }

    @DeleteMapping("management/delete-product/{productId}")
    public ResponseEntity<HttpStatusCode> deleteProduct(@PathVariable UUID productId){
        HttpStatusCode statusCode = productService.deleteProduct(productId);
        return ResponseEntity.status(statusCode).build();
    }

    // -------------  APIS FOR PRODUCT MANAGEMENT SERVICE [[END]] ---------------

    // -------------  APIS FOR PRODUCT CATEGORY SERVICE [[START]] ---------------

    @PostMapping("management/add-category")
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

    @PutMapping("management/update-category")
    public ResponseEntity<HttpStatusCode> updateCategory(@RequestBody Category category){
        return ResponseEntity.status(productService.updateCategory(category)).build();
    }

    @DeleteMapping("management/delete-category/{categoryId}")
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

    @PostMapping("management/add-color")
    public ResponseEntity<HttpStatusCode> saveColor(@RequestBody Color color){
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

    @PutMapping("management/update-color")
    public ResponseEntity<HttpStatusCode> updateColor(@RequestBody Color color){
        return ResponseEntity.status(productService.updateColor(color)).build();
    }

    @DeleteMapping("management/delete-color/{colorId}")
    public ResponseEntity<HttpStatusCode> deleteColor(@PathVariable long colorId){
        return ResponseEntity.status(productService.deleteColor(colorId)).build();
    }

    // ---------------  APIS FOR PRODUCT COLOR SERVICE [[END]] -----------------

    // ---------------  APIS FOR DISCOUNT SERVICE [[START]] -----------------

    @PostMapping("/management/add-new-discount")
    public ResponseEntity<HttpStatusCode> addDiscount(@RequestBody Discount discount){
        return discountService.addDiscount(discount);
    }

    @PutMapping("/management/edit-discount")
    public ResponseEntity<HttpStatusCode> editDiscount(@RequestBody Discount discount){
        return discountService.editDiscount(discount);
    }

    @PutMapping("/management/activate-discount/{discountId}")
    public ResponseEntity<HttpStatusCode> activateDiscount(@PathVariable long discountId){
        return discountService.activateDiscount(discountId);
    }

    @PutMapping("/management/deactivate-discount/{discountId}")
    public ResponseEntity<HttpStatusCode> deactivateDiscount(@PathVariable long discountId){
        return discountService.deactivateDiscount(discountId);
    }

    @GetMapping("/management/get-by-id/{discountId}")
    public ResponseEntity<DiscountResponse> getDiscountById(@PathVariable long discountId){
        return discountService.getById(discountId);
    }

    @GetMapping("/management/get-all-discounts-by-product-id/{productId}")
    public ResponseEntity<List<DiscountResponse>> getAllByProductId(@PathVariable UUID productId){
        return discountService.getAllByProductId(productId);
    }

    @GetMapping("/management/get-active-discount-by-product-id/{productId}")
    public ResponseEntity<DiscountResponse> getActiveByProductId(@PathVariable UUID productId){
        return discountService.getActiveByProductId(productId);
    }

    @GetMapping("/management/get-all-discounts")
    public ResponseEntity<List<DiscountResponse>> getAll(){
        return discountService.getAll();
    }

    @DeleteMapping("/management/delete-by-id/{discountId}")
    public ResponseEntity<HttpStatusCode> deleteById(@PathVariable long discountId){
        return discountService.deleteById(discountId);
    }

    // ---------------  APIS FOR DISCOUNT SERVICE [[END]] -----------------
}