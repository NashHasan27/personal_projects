package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/*Integration with the JPA H2 Database Hibernate ORM*/
@Entity
@Table(name = "claims")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId; // Unique identifier for the claim

    @Column(name="policy_id")
    private String policyId; // Associated policy identifier

    @Column(name="claimant_name")
    private String claimantName; // Name of the person submitting the claim

    @Column(name="claimant_contact")
    private String claimantContact; // Contact information for the claimant

    @Column(name="claim_date")
    private Date dateOfClaim; // Date when the claim was submitted

    @Column(name="incident_date")
    private Date incidentDate; // Date of the incident leading to the claim

    @Column(name="incident_description")
    private String descriptionOfIncident; // Description of the incident

    @Column(name="claim_amount")
    private double claimAmount; // Amount being claimed

    @Column(name="claim_status")
    private ClaimStatus claimStatus; // Current status of the claim

    // List of documents associated with the claim
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER, mappedBy = "claim",orphanRemoval = true)
    private List<Document> documents;
}
