package org.example.repository;

import org.example.model.CustomerServiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerRepository extends JpaRepository<CustomerServiceModel,Long> {

    //Performing Native Queries
    @Transactional
    @Modifying
    @Query("DELETE CustomerServiceModel c WHERE c.id = :id")
    void deleteCustomer(@Param("id") Long customerId);

    @Transactional
    @Query("SELECT c.email FROM CustomerServiceModel c WHERE c.id = :id")
    String retrieveCustomerEmail(@Param("id") Long customerId);
}
