package com.easyelectroshop.webscrappingservice.Repository;

import com.easyelectroshop.webscrappingservice.Model.WebScrapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface WebScrapperRepository extends JpaRepository<WebScrapper, Long> {

   public WebScrapper findAllByProductIdAndSite(UUID productId,String site);

   public List<WebScrapper> findAllByProductId(UUID productId);

}