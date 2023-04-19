package com.easyelectroshop.productcategorymanagementservice.Controller;

import com.easyelectroshop.productcategorymanagementservice.Model.Category;
import com.easyelectroshop.productcategorymanagementservice.Service.CategoryManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryManagementController {

    @Autowired
    CategoryManagementService categoryManagementService;


    @PostMapping("/add-category")
    public ResponseEntity<HttpStatusCode> saveCategory(@RequestBody Category category){
        return ResponseEntity.status(categoryManagementService.saveCategory(category)).build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> categories = categoryManagementService.getAllCategories();
        return (categories != null) ? ResponseEntity.ok(categories) : ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("/get-category/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable long categoryId){
        Optional<Category> category = categoryManagementService.getCategoryById(categoryId);
        return (category.isPresent()) ? ResponseEntity.ok(category.get()) : ResponseEntity.notFound().build();
    }

    @PutMapping("/edit-category")
    public ResponseEntity<HttpStatusCode> updateColor(@RequestBody Category category){
        return ResponseEntity.status(categoryManagementService.updateCategory(category)).build();
    }

    @DeleteMapping("/delete-category/{categoryId}")
    public ResponseEntity<HttpStatusCode> deleteCategory(@PathVariable long categoryId){
        return ResponseEntity.status(categoryManagementService.deleteCategory(categoryId)).build();
    }

}
