package org.example.repository;

import org.example.model.CustomerProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface ProfilePhotoRepository extends JpaRepository<CustomerProfilePhoto,Long> {

    @Transactional
    @Modifying
    @Query("DELETE CustomerProfilePhoto c where c.customer.id = :customerId")
    void deleteProfilePhoto(@Param("customerId") Long customerId);


    @Query("SELECT c.id FROM CustomerProfilePhoto c where c.customer.id = :customerId and c.fileName LIKE %:fileName%")
    Long getFileInfoById(@Param("customerId") Long customerId,
                         @Param("fileName") String fileName);

    @Query("SELECT COUNT(c) > 0 FROM CustomerProfilePhoto c WHERE c.customer.id = :customerId")
    boolean existsByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT c FROM CustomerProfilePhoto c where c.customer.id = :customerId")
    List<CustomerProfilePhoto> getProfilePhotoByCustomerId(@Param("customerId") Long customerId);

    @Transactional
    @Modifying
    @Query("UPDATE CustomerProfilePhoto c " +
            "SET c.filePath = :filePath, " +
            "c.fileName = :fileName, " +
            "c.fileType = :fileType, " +
            "c.uploadDate = :uploadDate " +
            "where c.customer.id = :customerId " +
            "and c.fileName LIKE %:fileName%")
    void updateProfilePhoto(@Param("customerId") Long customerId,
                            @Param("filePath") String filePath,
                            @Param("fileName") String fileName,
                            @Param("fileType") String fileType,
                            @Param("uploadDate") LocalDateTime uploadDate);
}
