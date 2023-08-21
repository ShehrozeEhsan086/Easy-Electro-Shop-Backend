package com.easyelectroshop.customermanagementservice.Repository;

import com.easyelectroshop.customermanagementservice.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerManagementRepository extends JpaRepository<Customer, UUID> {

    @Query(value = "SELECT * FROM customer ORDER BY ?1 ASC LIMIT ?2 OFFSET ?3 ",nativeQuery = true)
    List<Customer> findAllPaginated(String sortBy, int pageSize, int pageNumber);

    @Query(value = "SELECT CASE WHEN COUNT(user_name) = 0 THEN 'true' ELSE 'false' END AS result FROM customer WHERE user_name = ?1", nativeQuery = true)
    Boolean checkUserNameAvailability(String userName);

    Optional<Customer> findByEmail(String email);

}
