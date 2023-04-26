package com.easyelectroshop.productcategorymanagementservice.Service;

import com.easyelectroshop.productcategorymanagementservice.Model.Category;
import com.easyelectroshop.productcategorymanagementservice.Model.SubCategory;
import com.easyelectroshop.productcategorymanagementservice.Repository.CategoryManagementRepository;
import com.easyelectroshop.productcategorymanagementservice.Repository.SubCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryManagementService {

    @Autowired
    CategoryManagementRepository categoryManagementRepository;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    public HttpStatusCode saveCategory(Category category){
        log.info("ADDING CATEGORY WITH NAME "+category.getCategoryName());
        try{
            categoryManagementRepository.save(category);
            log.info("SUCCESSFULLY ADDED CATEGORY WITH NAME "+category.getCategoryName());
            return HttpStatusCode.valueOf(201);
        } catch (Exception ex){
            log.error("ERROR ADDING CATEGORY WITH NAME "+category.getCategoryName(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public List<Category> getAllCategories() {
        log.info("GETTING ALL CATEGORIES");
        try{
            List<Category> categories = categoryManagementRepository.findAll();
            log.info("SUCCESSFULLY RETRIEVE ALL CATEGORIES");
            return categories;
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE ALL CATEGORIES",ex);
            return null;
        }
    }

    public Optional<Category> getCategoryById(long categoryId) {
        log.info("GETTING CATEGORY WITH CATEGORY_ID "+categoryId);
        try{
            Optional<Category> category = categoryManagementRepository.findById(categoryId);
            if (category.isPresent()){
                log.info("SUCCESSFULLY RETRIEVED CATEGORY WITH CATEGORY_ID "+category);
                return category;

            } else {
                log.error("COULD NOT RETRIEVE CATEGORY WITH CATEGORY_ID "+categoryId+" CATEGORY NOT FOUND!");
                return Optional.empty();
            }
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE CATEGORY WITH CATEGORY_ID "+categoryId);
            return Optional.empty();
        }


    }

    public HttpStatusCode updateCategory(Category category) {
        log.info("EDITING CATEGORY WITH NAME "+category.getCategoryName());
        try{
            Optional<Category> tempCategory = categoryManagementRepository.findById(category.getCategoryId());
            if(tempCategory.isPresent()){
                categoryManagementRepository.save(category);
                log.info("SUCCESSFULLY EDITED CATEGORY WITH NAME "+category.getCategoryName());
                return HttpStatusCode.valueOf(202);
            } else {
                log.info("COULD NOT FIND GIVEN CATEGORY, ADDING NEW CATEGORY");
                categoryManagementRepository.save(category);
                log.info("SUCCESSFULLY ADDED NEW CATEGORY");
                return HttpStatusCode.valueOf(201);
            }
        } catch (Exception ex){
            log.error("ERROR EDITING CATEGORY WITH NAME "+category.getCategoryName(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode deleteCategory(long categoryId){
        log.info("DELETING CATEGORY WITH CATEGORY_ID "+categoryId);
        try{
            Optional<Category> category = categoryManagementRepository.findById(categoryId);
            if(category.isPresent()){
                categoryManagementRepository.deleteById(categoryId);
                log.info("SUCCESSFULLY DELETED CATEGORY WITH CATEGORY_ID "+categoryId);
                return HttpStatusCode.valueOf(200);
            } else {
                log.error("COULD NOT DELETE CATEGORY WITH CATEGORY_ID "+categoryId+" CATEGORY NOT FOUND!");
                return HttpStatusCode.valueOf(404);
            }
        } catch (Exception ex){
            log.error("COULD NOT DELETE CATEGORY WITH CATEGORY_ID "+categoryId);
            return HttpStatusCode.valueOf(500);
        }
    }


    public List<SubCategory> getSubCategories(long categoryId){
        log.info("GETTING SUB-CATEGORIES FOR CATEGORY WITH CATEGORY_ID "+categoryId);
        try{
            List<SubCategory> subCategories = subCategoryRepository.getSubCategoriesByCategoryId(categoryId);
            if (subCategories.isEmpty()){
                log.info("NO SUB-CATEGORIES FOR CATEGORY WITH CATEGORY_ID "+categoryId+" FOUND!");
                return null;
            } else {
                log.info("SUCCESSFULLY RETRIEVED SUB-CATEGORIES FOR CATEGORY WITH CATEGORY_ID "+categoryId);
                return subCategories;
            }
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE SUB-CATEGORIES FOR CATEGORY WITH CATEGORY_ID "+categoryId,ex);
            return null;
        }
    }
}