package org.example.service;

import org.example.dto.CustomerResponse;
import org.example.exception.FeignException;
import org.example.model.CustomerEducation;
import org.example.model.CustomerProfilePhoto;
import org.example.model.CustomerServiceModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {

    List<CustomerServiceModel> getAllCustomer();
    CustomerServiceModel submitCustomer(CustomerServiceModel customerServiceModel);
    CustomerServiceModel getCustomerById(Long id);
    List<CustomerServiceModel> getTop5CustomerBy(String by);
    boolean deleteCustomerById(Long id);
    CustomerProfilePhoto uploadProfilePhoto(Long id, MultipartFile profilePic) throws Exception;
    CustomerProfilePhoto updateProfilePhoto(Long id, MultipartFile updateProfilePic) throws Exception;
    byte[] getProfilePhoto(String profilePicPath) throws Exception;
    boolean deleteProfilePhotoById(Long id);
    String getCustomerEmailById(Long id);
    String convertDocsToStringBase64(MultipartFile file);
    String convertDocsToStringBase64Json(MultipartFile file);
    CustomerEducation submitCustomerEducation(Long id,CustomerEducation customerEducation) throws Exception;
    CustomerEducation updateCustomerEducation(Long id,CustomerEducation customerEducation) throws Exception;
    List<CustomerEducation> getTop3EducationBy(String by);
    CustomerResponse getCustomerResponseById(Long id);
    String feignExceptionProg(String feignName) throws FeignException;

    //Invoking a stored procedure
    String callSimpleProcedure(String input);

    //Invoking an insert transaction via stored procedure
    void insertCustomer(String firstName, String lastName, String email, String phoneNumber, String address);

    //Invoking a stored procedure -> GETALLCUSTOMERS from DB that will return the lists of all submitted customer data
    List<CustomerServiceModel> callCustomerProcedure();
}
