package org.example.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CustomerResponse;
import org.example.exception.CustomerNotFoundException;
import org.example.model.CustomerEducation;
import org.example.model.CustomerProfilePhoto;
import org.example.model.CustomerServiceModel;
import org.example.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("v1/customer")
@Slf4j
@OpenAPIDefinition(info = @Info(
        title = "Customer Service API Management",
        version = "1.0",
        description = "API for managing customer details and information"))
public class CustomerServiceController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceController.class);
    private final CustomerService customerService;

    public CustomerServiceController(CustomerService customerService){
        this.customerService = customerService;
    }

    @GetMapping("/init")
    public ResponseEntity<String> initCustomerService(){
        return ResponseEntity.ok().body("Customer Service Initiated!");
    }

    @GetMapping("/customerList")
    public ResponseEntity<List<CustomerServiceModel>> getAllCustomer(){
        List<CustomerServiceModel> customerList = customerService.getAllCustomer();
        return ResponseEntity.ok().body(customerList);
    }

    @GetMapping("/customer-response")
    public ResponseEntity<CustomerResponse> getCustomerResponse(@RequestParam("id") Long id){
        logger.info("CustomerResponse:: Received request to retrieve customer with ID {}", id);

        try {
            CustomerResponse response = customerService.getCustomerResponseById(id);
            logger.info("CustomerResponse:: Successfully retrieved customer with ID {}", id);
            return ResponseEntity.ok().body(response);
        } catch (CustomerNotFoundException e) {
            logger.error("CustomerResponse:: Customer not found for ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("CustomerResponse:: Error retrieving customer with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/customer-id")
    public ResponseEntity<CustomerServiceModel> getCustomerById(@RequestParam("id") Long id){
        logger.info("Received request to retrieve customer with ID {}", id);

        try {
            CustomerServiceModel customerById = customerService.getCustomerById(id);
            logger.info("Successfully retrieved customer with ID {}", id);
            return ResponseEntity.ok().body(customerById);
        } catch (CustomerNotFoundException e) {
            logger.error("Customer not found for ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Error retrieving customer with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/customer-education/by")
    public ResponseEntity<List<CustomerEducation>> getTop3CustEducation(@RequestParam("by") String by){
        List<CustomerEducation> top3CustEdu = customerService.getTop3EducationBy(by);
        return ResponseEntity.ok().body(top3CustEdu);
    }

    @GetMapping("/customer-list/by")
    public ResponseEntity<List<CustomerServiceModel>> getTop5CustomerList(@RequestParam("by") String by){
        List<CustomerServiceModel> top5CustomerList = customerService.getTop5CustomerBy(by);
        return ResponseEntity.ok().body(top5CustomerList);
    }

    @PostMapping("/submitCustomer")
    public ResponseEntity<CustomerServiceModel> submitCustomer(@RequestBody CustomerServiceModel customerServiceModel){
        CustomerServiceModel customerServiceAppl = customerService.submitCustomer(customerServiceModel);

        return new ResponseEntity<>(customerServiceAppl,HttpStatus.OK);
    }

    @DeleteMapping("/deleteCustomer")
    public ResponseEntity<String> deleteCustomer(@RequestParam("id") Long id) {
        boolean deleteCustById = customerService.deleteCustomerById(id);

        if(deleteCustById) {
            return new ResponseEntity<>("Successfully Removed Customer!",HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Unsuccessful in Removing Customer",HttpStatus.BAD_REQUEST);
        }
    }

    ///Submitting and uploading image/picture into the server and backend ecosystem
    @PostMapping("/uploadProfilePic/{customerId}")
    public ResponseEntity<?> uploadCustomerImage(@PathVariable("customerId") Long customerId,
                                                 @RequestParam("profile") MultipartFile profilePic) throws Exception {
        try{
            CustomerProfilePhoto customerProfilePhoto = customerService.uploadProfilePhoto(customerId,profilePic);
            return new ResponseEntity<>(customerProfilePhoto,HttpStatus.OK);// status 200 and response body
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to profile picture.");
        }
    }

    ///Retrieving and displaying back to the UI the submitted image/picture
    @GetMapping("/displayProfilePic/{storagePath:.+}")
    public ResponseEntity<byte[]> getProfilePic(@PathVariable String storagePath) throws Exception {
        try {
            //Declaring the byte data as the aim is to return/writing the full image/docs back
            byte[] profilePicContent = customerService.getProfilePhoto(storagePath);

            //Determine media type from file extension and its mimetype
            String lowerCasePath = storagePath.toLowerCase();
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM; //fallback to default application mime type

            if (lowerCasePath.endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            }
            else if (lowerCasePath.endsWith(".jpg") || lowerCasePath.endsWith(".jpeg")) {
                mediaType = MediaType.IMAGE_JPEG;
            }

            /// Returning the image/profile photo that is uploaded
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(mediaType)
                    .body(profilePicContent);

        } catch (
                FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/updateProfilePic/{customerId}")
    public ResponseEntity<?> reuploadProfilePic(@PathVariable("customerId") Long customerId,
                                                @RequestParam("profile") MultipartFile profilePic) throws Exception {
        try {
            // Validate the MultipartFile
            if (profilePic.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Profile picture file is empty.");
            }

            // Update the profile photo by invoking the updateProfilePhoto method from service class
            CustomerProfilePhoto customerProfilePhoto = customerService.updateProfilePhoto(customerId, profilePic);

            // Return success response
            return ResponseEntity.ok(customerProfilePhoto);

        } catch (EntityNotFoundException e) {
            // Handle specific exception when customer or profile photo is not found
            logger.error("Customer or profile photo not found: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer or profile photo not found.");

        } catch (IOException e) {
            // Handle IO exceptions separately
            logger.error("Error processing the profile picture: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the profile picture.");

        } catch (Exception e) {
            // Handle general exceptions
            // Adding more context to the logging message to provide more visibility of the error and issue occurred
            logger.error("An unexpected error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred." + e.getMessage());
        }
    }

    ///Removing and delete the profile photo that has been uploaded parameterized by customer ID
    @DeleteMapping("/deleteProfile")
    public ResponseEntity<String> deleteProfilePhoto(@RequestParam("id") Long id) {
        boolean deleteProfilePhotoById = customerService.deleteProfilePhotoById(id);

        if(deleteProfilePhotoById) {
            return ResponseEntity.ok("Successfully Removed Customer's Profile Photo!");
        }
        else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/data")
    public Map<String, Object> sendData() {
        Map<String, Object> MapResponse = new HashMap<>();
        MapResponse.put("message", "HashMap Implementation");
        MapResponse.put("timestamp", System.currentTimeMillis());
        MapResponse.put("data structure","HashMap");
        MapResponse.put("service","Customer-Service");

        return MapResponse;
    }

    @GetMapping("/customer-email/{id}")
    public ResponseEntity<String> getCustomerEmail(@PathVariable("id") Long id){
        String customerEmail = customerService.getCustomerEmailById(id);
        return new ResponseEntity<>(customerEmail,HttpStatus.OK);
    }

    @PostMapping("/submit-education/{id}")
    public ResponseEntity<CustomerEducation> submitCustomerEducation(@PathVariable("id") Long id,
                                                                     @RequestBody CustomerEducation customerEducation) throws Exception {

        customerEducation = customerService.submitCustomerEducation(id, customerEducation);
        return new ResponseEntity<>(customerEducation,HttpStatus.OK);
    }

    @PutMapping("/update-education/{id}")
    public ResponseEntity<CustomerEducation> updateCustomerEducation(@PathVariable("id") Long id,
                                                                     @RequestBody CustomerEducation customerEducation) throws Exception {

        customerEducation = customerService.updateCustomerEducation(id, customerEducation);
        return new ResponseEntity<>(customerEducation,HttpStatus.OK);
    }

    ///Feign-Client implementation
    ///With the aim to enable communication between claim-service & customer-service
    /// {
        @PostMapping(value= "/convertDocs-base64",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
            //Invoking the service class method that will responsibly handle the logic process in converting to String Base64 format
            String base64String = customerService.convertDocsToStringBase64(file);
            return ResponseEntity.ok(base64String);
        }

        @PostMapping("/convertDocs-base64/json-format")
        public ResponseEntity<Map<String,String>> uploadDocumentBase64Json(@RequestParam("file") MultipartFile file) {
            try {
                //Invoking the service class method that will responsibly handle the logic process
                String base64String = customerService.convertDocsToStringBase64Json(file);

                //Create a response map with a key-value pair with Map structure
                Map<String, String> response = new HashMap<>();
                response.put("fileContent", base64String);

                return ResponseEntity.ok(response);
            } catch (Exception e) {

                // Create an error response map
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Failed to encode file");

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }
    /// }
}
