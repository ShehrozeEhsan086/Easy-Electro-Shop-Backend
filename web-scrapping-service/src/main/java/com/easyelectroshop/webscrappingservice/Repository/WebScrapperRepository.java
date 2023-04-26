package com.easyelectroshop.webscrappingservice.Repository;

import com.easyelectroshop.webscrappingservice.Model.WebScrapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebScrapperRepository extends JpaRepository<WebScrapper, Long> {

    public WebScrapper findByProductIdAndSite(UUID productId,String site);

}