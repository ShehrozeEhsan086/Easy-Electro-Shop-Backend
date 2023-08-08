package com.easyelectroshop.customermanagementservice.Repository;

import com.easyelectroshop.customermanagementservice.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerManagementRepository extends JpaRepository<Customer, UUID> {

}
