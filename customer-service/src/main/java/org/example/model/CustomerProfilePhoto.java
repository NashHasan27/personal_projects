package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*Integration with the JPA H2 Database Hibernate ORM*/
@Entity
@Table(name = "customer_profile")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProfilePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="file_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private CustomerServiceModel customer;

    @Column(name="file_path")
    private String filePath; //Path or URL to the photo in the file repository

    @Column(name="file_name")
    private String fileName; //Name of the file

    @Column(name="file_type")
    private String fileType; //Type of the file (e.g., image/jpeg/png)

    @Column(name="upload_date")
    private LocalDateTime uploadDate; //Date and time of upload

}
