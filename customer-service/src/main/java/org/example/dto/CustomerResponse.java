package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.CustomerProfilePhoto;
import org.example.model.CustomerServiceModel;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {

    private CustomerServiceModel customer;
    private List<CustomerProfilePhoto> profile;
}
