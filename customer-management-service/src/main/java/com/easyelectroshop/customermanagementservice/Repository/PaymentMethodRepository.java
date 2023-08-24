package com.easyelectroshop.customermanagementservice.Repository;

import com.easyelectroshop.customermanagementservice.Model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

}
