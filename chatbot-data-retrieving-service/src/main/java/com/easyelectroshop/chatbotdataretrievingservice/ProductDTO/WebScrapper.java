package com.easyelectroshop.chatbotdataretrievingservice.ProductDTO;

import java.util.UUID;

public record WebScrapper(
    long scrapperId,
    UUID productId,
    String site,
    String scrappedPrice,
    boolean visible
) {
}
