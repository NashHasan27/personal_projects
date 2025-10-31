package org.example.model;

public enum ClaimStatus {
        SUBMITTED,    // Claim has been submitted but not yet reviewed
        UNDER_REVIEW, // Claim is currently being reviewed
        APPROVED,     // Claim has been approved
        REJECTED,     // Claim has been rejected
        CLOSED        // Claim has been closed after resolution
}
