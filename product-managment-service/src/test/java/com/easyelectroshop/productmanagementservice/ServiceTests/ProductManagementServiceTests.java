package com.easyelectroshop.productmanagementservice.ServiceTests;

import com.easyelectroshop.productmanagementservice.Model.Product;
import com.easyelectroshop.productmanagementservice.Model.ProductImage;
import com.easyelectroshop.productmanagementservice.Model.SubCategory;
import com.easyelectroshop.productmanagementservice.Service.ProductManagementService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class ProductManagementServiceTests {

    @Autowired
    ProductManagementService productManagementService;
    private static Product product = new Product();

    private static UUID productId = new UUID(0,0).randomUUID();

//    @BeforeAll
//    public static void init(){
//        ProductImage image = new ProductImage();
//        image.setImageData("imageBase64Data");
//        image.setColors(1);
//        ProductImage imageTwo = new ProductImage();
//        imageTwo.setImageData("imageBase64Data");
//        imageTwo.setColors(1);
//        ArrayList<ProductImage> images = new ArrayList<>();
//        images.add(image);
//        images.add(imageTwo);
//        product.setProductId(productId);
//        product.setName("Test Product");
//        product.setImages(images);
//        product.setShortDescription("Short Description");
//        product.setCompleteDescription("Complete Description");
//        product.setCoverImage("coverImageBase64Data");
//        product.setBrandName("Test Brand");
//        product.setPrice(1500);
//        product.setDiscountPercentage(0);
//        product.setDiscountedPrice(0);
//        product.setQuantity(0);
//        product.setSize(0);
//        ArrayList<Long> colors = new ArrayList<>();
//        colors.add(1L);
//        colors.add(2L);
//        product.setColors(colors);
//        product.setCategory(15L);
//        ArrayList<SubCategory> subCategories = new ArrayList<>();
//        SubCategory subCategory = new SubCategory();
//        subCategory.setSubCategoryFkId(151);
//        subCategories.add(subCategory);
//        product.setSubCategories(subCategories);
//        product.set_3DModelFilename("filename.glb");
//        product.set_3DModelURL("s3/filename.glb");
//        product.setAvailable(true);
//        product.setLastUpdated(Instant.now().toString());
//        product.setDiscounted(false);
//    }
//
//    @Test
//    public void testSaveProduct(){
//        HttpStatusCode result = productManagementService.saveProduct(product);
//        Assertions.assertEquals(HttpStatusCode.valueOf(201),result);
//    }
//
//    @Test
//    public void testGetProduct(){
//        Optional<Product> result = productManagementService.getProductById(productId);
//        Assertions.assertSame(product,result);
//    }

}
