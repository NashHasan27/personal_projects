package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*Integration with the JPA H2 Database Hibernate ORM*/
@Entity
@Table(name = "customer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerServiceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="customer_id")
    private Long id;

    @Column(name="customer_firstname")
    private String firstName;

    @Column(name="customer_lastname")
    private String lastName;

    @Column(name="customer_email")
    private String email;

    @Column(name="customer_phoneNo")
    private String phoneNumber;

    @Column(name="customer_address")
    private String address;

}
