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
@NamedStoredProcedureQuery(
        name = "InsertCustomerProcedure",
        procedureName = "INSERT_CUSTOMER",
        resultClasses = CustomerServiceModel.class,
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "firstName", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "lastName", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "email", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "phoneNumber", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "address", type = String.class)
        }
)
@NamedStoredProcedureQuery(
        name = "getAllCustomers",
        procedureName = "GETALLCUSTOMERS",
        resultClasses = CustomerServiceModel.class
)

public class CustomerServiceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="customer_id")
    private Integer id;

    @Column(name="customer_firstname")
    private String firstName;

    @Column(name="customer_lastname")
    private String lastName;

    @Column(name="customer_email")
    private String email;

    @Column(name="customer_phone_no")
    private String phoneNumber;

    @Column(name="customer_address")
    private String address;

}
