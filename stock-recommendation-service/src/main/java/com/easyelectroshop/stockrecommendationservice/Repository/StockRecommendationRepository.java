package com.easyelectroshop.stockrecommendationservice.Repository;

import com.easyelectroshop.stockrecommendationservice.Model.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StockRecommendationRepository extends JpaRepository<DataEntity, Long> {

    DataEntity findByProductId(UUID productId);

}
