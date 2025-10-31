package org.example.service;

import org.example.model.CustomerEducation;
import org.example.model.CustomerProfilePhoto;
import org.example.model.CustomerServiceModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {

    List<CustomerServiceModel> getAllCustomer();
    CustomerServiceModel submitCustomer(CustomerServiceModel customerServiceModel);
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
}
