package org.example.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.client.ClaimClient;
import org.example.model.CustomerEducation;
import org.example.model.CustomerProfilePhoto;
import org.example.model.CustomerServiceModel;
import org.example.repository.CustomerEducationRepository;
import org.example.repository.CustomerRepository;
import org.example.repository.ProfilePhotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
@Component
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private static final String STORAGE_DIRECTORY = "Customer_Profiles";

    private final CustomerRepository customerRepository;
    private final ProfilePhotoRepository profilePhotoRepository;
    private final CustomerEducationRepository customerEducationRepository;
    private final ClaimClient claimClient;

    @Override
    @Cacheable(value = "customer")
    public List<CustomerServiceModel> getAllCustomer(){
        return customerRepository.findAll();
    }

    @Override
    @Transactional
    @Retry(name = "customerRetry", fallbackMethod = "createCustomerFallBack")
    @RateLimiter(name = "customerRateLimiter", fallbackMethod = "createCustomerFallBack")
    @CircuitBreaker(name = "customerCircuitBreaker", fallbackMethod = "createCustomerFallBack")
    public CustomerServiceModel submitCustomer(CustomerServiceModel customerServiceModel) {
        //throw new RuntimeException("Simulated Max Attempts Retry failure");
        return customerRepository.save(customerServiceModel);
    }

    @Override
    @Transactional
    @CacheEvict(value="customer",key="#id")
    public boolean deleteCustomerById(Long id) {
        try {
            //#1 Step to delete the dependent table CUSTOMER_PROFILE
            profilePhotoRepository.deleteProfilePhoto(id);
            logger.info("Removed from CUSTOMER_PROFILE table, ID {}", id);

            //#2 Step to delete and remove from the main table CUSTOMER
            customerRepository.deleteCustomer(id);
            logger.info("Removed from CUSTOMER table, ID {}", id);

            logger.info("Successfully deleted customer with ID {}", id);
            return true;
        } catch (Exception e) {
            // Log the exception if necessary
            System.err.println("Error deleting customer with ID " + id + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public CustomerProfilePhoto uploadProfilePhoto(Long id,MultipartFile profilePic) throws Exception {

        //Step 1:Ensure and pre-checking whether the claim ID really exists
        CustomerServiceModel customerServiceModel = customerRepository.findById(id).orElseThrow(() -> new Exception("Customer not found"));

        //Step 2:Ensure and pre-checking the storage directory exists
        Path storagePath = Paths.get(STORAGE_DIRECTORY);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        //Optional Step(#1): Generate a unique file name to prevent overwriting
        String uniqueFileName = "Profile" + "_" + id + "_" + profilePic.getOriginalFilename();
        Path filePath = storagePath.resolve(uniqueFileName);

        try {
            //Step 3: Write the file to the storage directory
            Files.write(filePath, profilePic.getBytes());
        } catch (IOException ioException) {
            logger.error("Error writing file to storage: {}", uniqueFileName, ioException);
            throw new RuntimeException("Failed to store file: " + uniqueFileName, ioException);
        }

        //Optional Step(#2): Store clean relative path, not OS-specific absolute path
        String relativePath = STORAGE_DIRECTORY + uniqueFileName;

        //Step 4: To assign the values from Multipart file object into the CustomerProfilePhoto Entity Object
        CustomerProfilePhoto customerProfilePhoto = new CustomerProfilePhoto();
        customerProfilePhoto.setFileName(profilePic.getOriginalFilename());
        customerProfilePhoto.setFileType(profilePic.getContentType());
        customerProfilePhoto.setFilePath(relativePath);
        customerProfilePhoto.setUploadDate(LocalDateTime.now());
        customerProfilePhoto.setCustomer(customerServiceModel);

        //Step 5: Finally to save the submitted data into Database
        profilePhotoRepository.save(customerProfilePhoto);

        //Step 6: Returning the Customer Object as JSON
        return customerProfilePhoto;
    }

    @Override
    @Transactional
    public CustomerProfilePhoto updateProfilePhoto(Long id, MultipartFile updateProfilePic) throws Exception {

        // Step 1: Ensure the storage directory exists and create directory
        Path storagePath = Paths.get(STORAGE_DIRECTORY);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        try {

            // Step 2: Retrieve the existing profile photo
            logger.info("The original file name submitted is : {}",updateProfilePic.getOriginalFilename());
            Long fileId = profilePhotoRepository.getFileInfoById(id,updateProfilePic.getOriginalFilename());

            logger.info("The file ID retrieved from CUSTOMER_PROFILE table is :  {}",fileId);
            CustomerProfilePhoto existingProfilePhoto = profilePhotoRepository.findById(fileId)
                    .orElseThrow(() -> new EntityNotFoundException("Profile Photo not found with ID: " + id));

            // Step 3: Generate a unique file name for the new profile photo
            String uniqueFileName = "Profile" + "_" + id + "_" + "Updated" + "_" + updateProfilePic.getOriginalFilename();
            Path newFilePath = storagePath.resolve(uniqueFileName);

            // Step 4: Update the profile photo entity with new details and information
            existingProfilePhoto.setFileName(updateProfilePic.getOriginalFilename());
            existingProfilePhoto.setFileType(updateProfilePic.getContentType());
            existingProfilePhoto.setFilePath(STORAGE_DIRECTORY + uniqueFileName);
            existingProfilePhoto.setUploadDate(LocalDateTime.now());

            //Step 5: Update the profile photo in the database
            profilePhotoRepository.updateProfilePhoto(id,
                    existingProfilePhoto.getFilePath(),
                    existingProfilePhoto.getFileName(),
                    existingProfilePhoto.getFileType(),
                    existingProfilePhoto.getUploadDate());

            // Step 6: Delete the existing file from storage
            String newFilename = existingProfilePhoto.getFileName();
            logger.info("File name from Database is : {}", newFilename);

            Files.list(storagePath)
                    .filter(filePath -> filePath.getFileName().toString().contains(newFilename))
                    .forEach(filePath -> {
                        logger.info("The file path is : {}", filePath);
                        try {
                            Files.deleteIfExists(filePath);
                            logger.info("File deleted successfully from file storage before updating: {}", filePath.getFileName());
                        } catch (IOException ioException) {
                            logger.error("Error deleting file: {}", filePath.getFileName(), ioException);
                            throw new RuntimeException("Failed to delete file: " + filePath.getFileName(), ioException);
                        }
                    });

            // Step 7: Write the new file to the storage directory
            Files.write(newFilePath, updateProfilePic.getBytes());

            // Step 8: Retrieve and return the updated Profile Photo object
            return existingProfilePhoto;

        } catch (IOException e) {
            logger.error("Error accessing storage directory: {}", STORAGE_DIRECTORY, e);
            throw new RuntimeException("Failed to access storage directory: " + STORAGE_DIRECTORY, e);
        }

    }

    @Override
    public byte[] getProfilePhoto(String profilePicPath) throws Exception {
        //The STORAGE_DIRECTORY to locate/access to local root folder (e.g., "Customer_Profiles")
        Path storageDir = Paths.get(STORAGE_DIRECTORY).toAbsolutePath().normalize();

        //Step 1: Normalize DB path (replace backslashes if necessary)
        String normalizedDbPath = profilePicPath.replace("\\", "/");

        //Step 2: Extract part after "Customer_Profiles/"
        String relativePath;
        String folderPrefix = "Customer_Profiles/";
        logger.info("Path from DB : {} ",profilePicPath);
        if (normalizedDbPath.startsWith(folderPrefix)) {
            relativePath = normalizedDbPath.substring(folderPrefix.length());
        }
        else {
            relativePath = normalizedDbPath; //If the DB path doesn't start with folderPrefix, assume it is just the file name
        }

        //Step 3: Build the full path by combining local storage folder + relative file name
        Path fullFilePath = storageDir.resolve(relativePath).normalize();
        logger.info("Resolved file path: " + fullFilePath);

        if (!Files.exists(fullFilePath)) {
            throw new FileNotFoundException("Profile photo not found: " + fullFilePath);
        }
        return Files.readAllBytes(fullFilePath);
    }

    @Override
    @Transactional
    public boolean deleteProfilePhotoById(Long id) {
        try {
            //Step 1: Retrieving the CustomerProfilePhoto object to check if it exists by customer id --> Checking Process
            CustomerProfilePhoto customerProfilePhoto = profilePhotoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Profile Photo not found with ID: " + id));

            try {
                //Step 2: Get the full path of the storage directory
                Path storagePath = Paths.get(STORAGE_DIRECTORY);

                //Step 3: List all files in the storage directory
                    // Use Files.list to iterate over files in the Customer_Profiles file server directory.
                    // Implementing and using the Lambda Stream package to stream along the files within the directory
                Files.list(storagePath).forEach(filePath -> {
                    try {
                            //Step 4: Pre-checking the file name contains any document name from the database
                                // If it contains & matches the file from the file storage will be deleted as well
                            String profilePhoto = customerProfilePhoto.getFileName();
                            logger.info("The filename is : {}",profilePhoto);
                            if (filePath.getFileName().toString().contains(profilePhoto)) {
                                Files.deleteIfExists(filePath); // Delete the file from storage
                                logger.info("File deleted successfully from file storage: {}", filePath.getFileName());
                            }
                    } catch (IOException ioException) {
                        logger.error("Error deleting file: {}", filePath.getFileName(), ioException);
                        throw new RuntimeException("Failed to delete file: " + filePath.getFileName(), ioException);
                    }
                });

            } catch (IOException e) {
                logger.error("Error accessing storage directory: {}", STORAGE_DIRECTORY, e);
                throw new RuntimeException("Failed to access storage directory: " + STORAGE_DIRECTORY, e);
            }

            //Step 5: Proceed to remove & delete the metadata of the file from database
            profilePhotoRepository.deleteProfilePhoto(id);

            logger.info("Successfully deleted customer's profile photo with ID {}", id);
            return true;

        } catch (EntityNotFoundException entityNotFoundException) {
            logger.error("Profile Photo not found with ID: {}", id, entityNotFoundException);
            return false;

        } catch (Exception e) {
            logger.error("Error deleting customer's profile photo with ID: {}", id, e);
            return false;

        }

    }

    @Override
    public CustomerEducation submitCustomerEducation(Long id,CustomerEducation customerEducation) throws Exception {
        //Step 1:Ensure and pre-checking whether the claim ID really exists
        //CustomerServiceModel customerServiceModel = customerRepository.findById(id).orElseThrow(() -> new Exception("Customer not found"));

        //Step 2: Assigning the data/values retrieved from the Request Body by the user
        CustomerEducation customerEducationInfo = getCustomerEducation(id, customerEducation);

        //Step 3: Saving into DB and returning the data
        return customerEducationRepository.save(customerEducationInfo);
    }

    @Override
    public CustomerEducation updateCustomerEducation(Long id, CustomerEducation customerEducation) throws Exception {
        //Implementation of the business logic
        CustomerEducation customerEducationInfo = getCustomerEducation(id, customerEducation);

        //Invoking the update method handled at the repository class
            customerEducationRepository.updateCustomerEducation(id,
                                                                customerEducation.getEducationName(),
                                                                customerEducation.getStartDate(),
                                                                customerEducation.getEndDate(),
                                                                customerEducation.getEducationStatus(),
                                                                customerEducation.getEducationDescription());

        return customerEducationInfo;
    }

    private static CustomerEducation getCustomerEducation(Long id, CustomerEducation customerEducation) {
        CustomerEducation customerEducationInfo = new CustomerEducation();
        customerEducationInfo.setCustomerId(id);
        customerEducationInfo.setEducationName(customerEducation.getEducationName());
        customerEducationInfo.setEducationDescription(customerEducation.getEducationDescription());
        customerEducationInfo.setStartDate(customerEducation.getStartDate());
        customerEducationInfo.setEndDate(customerEducation.getEndDate());
        customerEducationInfo.setEducationStatus(customerEducation.getEducationStatus());
        return customerEducationInfo;
    }

    @Override
    @Transactional
    public String getCustomerEmailById(Long id) {
        return customerRepository.retrieveCustomerEmail(id);
    }

    //fallback method leveraging through the Resilience4j package
    public CustomerServiceModel createCustomerFallBack(CustomerServiceModel customerServiceModel, Throwable throwable) {
        logger.error("Fallback occurred due to : {}", throwable.getMessage());
        return new CustomerServiceModel();
    }

    ///Feign-Client to consume API from claims-service
    /// {
        @Override
        @Transactional
        public String convertDocsToStringBase64(MultipartFile document){
            logger.info("CustomerService:convertDocsToStringBase64 execution started.");
            return claimClient.convertDocsToStringBase64(document);
        }

        @Override
        @Transactional
        public String convertDocsToStringBase64Json(MultipartFile file) {
            logger.info("CustomerService:convertDocsToStringBase64Json execution started.");
            return claimClient.convertDocsToStringBase64Json(file);
        }
    /// }
}
