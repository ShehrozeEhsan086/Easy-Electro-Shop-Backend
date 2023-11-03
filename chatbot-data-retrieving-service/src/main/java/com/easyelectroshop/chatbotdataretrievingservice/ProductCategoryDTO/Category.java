package com.easyelectroshop.chatbotdataretrievingservice.ProductCategoryDTO;

import java.util.List;

public record Category(long categoryId, String categoryName, List<SubCategory> subCategories) {
}
