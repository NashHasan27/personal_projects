package org.example.repository;

import jakarta.transaction.Transactional;
import org.example.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document,Long> {

    // Native queries handling to perform SELECT,UPDATE,DELETE to database table Document
    @Modifying
    @Transactional
    @Query("DELETE FROM Document d WHERE d.claim.id = :claimId")
    void deleteByClaimId(@Param("claimId") Long claimId);

    @Query("SELECT d FROM Document d WHERE d.claim.id = :claimId")
    List<Document> getAllDocsById(@Param("claimId") Long claimId);

    @Query("SELECT d FROM Document d")
    List<Document> getAllClaimDocs();


}
