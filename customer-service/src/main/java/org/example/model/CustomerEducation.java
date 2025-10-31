package org.example.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer_education")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEducation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    //@ManyToOne
    //@JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name="institution")
    private String educationName; // The institute or university joined

    @Column(name="start_date")
    private String startDate; // Start/Join date

    @Column(name="end_date")
    private String endDate; // End date

    @Column(name="status")
    private String educationStatus; // Status of the education

    @Column(name="major")
    private String educationDescription;
}
