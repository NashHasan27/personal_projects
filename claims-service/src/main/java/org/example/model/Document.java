package org.example.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId; // Unique identifier for the document

    @Column(name="document_name")
    private String documentName; // Name of the document

    @Column(name="document_type")
    private String documentType; // Type of the document (e.g., PDF, JPEG)

    @Column(name="document_path")
    private String documentPath;

    @Column(name="document_size")
    private Long documentSize;

    @Column(name="upload_date")
    private LocalDateTime uploadDate;

    @ManyToOne(fetch = FetchType.LAZY) // Declaration of the relationship between tables
    @JoinColumn(name = "claim_id")
    @JsonIgnore // Prevent serialization of claim to avoid deep nesting
    private Claim claim; // Reference to Claim for bidirectional relationship
}
