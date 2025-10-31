package org.example.repository;

import org.example.model.CustomerEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface CustomerEducationRepository extends JpaRepository<CustomerEducation,Long> {

    @Transactional
    @Modifying
    @Query("UPDATE CustomerEducation c " +
            "SET c.educationName = :educationName, " +
            "c.startDate = :startDate, " +
            "c.endDate = :endDate, " +
            "c.educationStatus = :educationStatus, " +
            "c.educationDescription = :educationDescription " +
            "where c.customerId = :customerId")
    void updateCustomerEducation(@Param("customerId") Long customerId,
                                 @Param("educationName") String educationName,
                                 @Param("startDate") String startDate,
                                 @Param("endDate") String endDate,
                                 @Param("educationStatus") String educationStatus,
                                 @Param("educationDescription") String educationDescription);

}
