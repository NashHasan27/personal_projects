package org.example.repository;

import org.example.model.Claim;
import org.example.model.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    // Native queries handling to perform SELECT,UPDATE,DELETE to database table Claim
    // Query for transactional to enable update action in updating Claim Object values based on ClaimId
    @Transactional
    @Modifying
    @Query("UPDATE Claim c " +
            "SET c.claimAmount = :claimAmount," +
            "c.policyId = :policyId," +
            "c.claimantName = :claimantName," +
            "c.claimantContact = :claimantContact," +
            "c.dateOfClaim= :dateOfClaim, " +
            "c.incidentDate = :incidentDate, " +
            "c.descriptionOfIncident = :descriptionOfIncident, " +
            "c.claimStatus = :claimStatus " +
            "WHERE c.id = :id")
    void updateClaimInfo(@Param("id") Long claimId,
                         @Param("claimAmount") double claimAmount,
                         @Param("policyId") String policyId,
                         @Param("claimantName") String claimantName,
                         @Param("claimantContact") String claimantContact,
                         @Param("dateOfClaim")Date dateOfClaim,
                         @Param("incidentDate") Date incidentDate,
                         @Param("descriptionOfIncident") String descriptionOfIncident,
                         @Param("claimStatus") ClaimStatus claimStatus);

    //Query for transactional to enable delete action in delete Claim values based on ClaimId
    @Modifying
    @Transactional
    @Query("DELETE FROM Claim c WHERE c.claimId = :id")
    void deleteClaimById(@Param("id")Long claimId);


}
