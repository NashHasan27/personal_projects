package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "configuration")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="service_id")
    private String serviceId;

    @Column(name="service_path")
    private String servicePath;

    @Column(name="service_name")
    private String serviceName;

}
