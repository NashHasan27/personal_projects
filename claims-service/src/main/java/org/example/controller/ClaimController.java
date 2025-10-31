package org.example.controller;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.example.model.Claim;
import org.example.model.CustomerServiceModel;
import org.example.model.Document;
import org.example.service.ClaimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@ControllerAdvice
@RequestMapping("v1/claim")
@OpenAPIDefinition(info = @Info(
                                    title = "Claim Management API",
                                    version = "1.0",
                                    description = "API for managing claim submission and documents associated with claims")
                                )
public class ClaimController {

    private static final Logger logger = LoggerFactory.getLogger(ClaimController.class);

    private final ClaimService claimService;

    @Autowired
    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping("/register")
    public ResponseEntity<String> registerClaimService(){
        return ResponseEntity.ok().body("Claim Service Registered!");
    }

    @GetMapping("/claimList")
    public ResponseEntity<List<Claim>> receiveAllClaims(){
        List<Claim> claims = claimService.getAllClaims();

        if (claims != null){
            return ResponseEntity.ok().body(claims);
        }
        else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/claimDocs")
    public ResponseEntity<List<Document>> receiveAllDocClaims(){
        List<Document> docClaims = claimService.getAllClaimDocs();

        if (docClaims != null){
            return ResponseEntity.ok().body(docClaims);
        }
        else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/claimDocs/{claimId}")
    public ResponseEntity<List<Document>> receiveClaimDocsById(@PathVariable("claimId") Long claimId){
        List<Document> document = claimService.getClaimDocsById(claimId);

        if (document != null){
            return ResponseEntity.ok().body(document);
        }
        else {
            return ResponseEntity.notFound().build();
        }

    }

    //REST API exposed that will handle the download document functionality
    @GetMapping("/{documentPath}")
    public ResponseEntity<byte[]> downloadClaimDocs(@PathVariable String documentPath) throws IOException {

        try {
            byte[] documentContent = claimService.getClaimDocsContent(documentPath);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + documentPath);
            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

            return new ResponseEntity<>(documentContent, headers, HttpStatus.OK);
        }catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<Claim> submitClaim(@RequestBody Claim claim){
        Claim submittedClaim = claimService.submitClaim(claim);
        return ResponseEntity.ok(submittedClaim);
    }

    @PostMapping("/uploadDocuments/{claimId}")
    public ResponseEntity<List<Document>> uploadClaimDocument(@PathVariable("claimId") Long claimId,
                                                      @RequestParam("documents") List<MultipartFile> documents) {

        logger.info("ClaimController::uploadClaimDocument execution starting");
        List<Document> docValue = null;
        try {
            docValue = claimService.submitClaimDocs(claimId, documents);
            return ResponseEntity.ok(docValue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(docValue);
        }
    }

    //Uploading document with base64 methodology approach
    @PostMapping("/uploadDocs-base64/{claimId}")
    public ResponseEntity<Document> uploadClaimDocumentBase64(@PathVariable("claimId") Long claimId,
                                                              @RequestBody List<String> base64claimDocs,
                                                              @RequestParam("filename") String filename) throws Exception {

        //Invoking & callback the base64 submission service for implementation in API endpoint
        Document base64Document = claimService.submitClaimDocsBase64(claimId,base64claimDocs,filename);
        try{
            return ResponseEntity.ok(base64Document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //API endpoint to enable conversion of any MIME types into String base64 format
    @PostMapping("/convertDocs-base64")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            //Invoking the service class method that will responsibly handle the logic process
            String base64String = claimService.convertDocsToStringBase64(file);

            return ResponseEntity.ok(base64String);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to encode file");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //API endpoint to enable conversion of any MIME types into String base64 format and return as JSON
    @PostMapping("/convertDocs-base64/json-format")
    public ResponseEntity<Map<String,String>> uploadDocumentBase64Json(@RequestParam("file") MultipartFile file) {
        try {
            //Invoking the service class method that will responsibly handle the logic process
            String base64String = claimService.convertDocsToStringBase64(file);

            // Create a response map with a key-value pair with Map structure
            Map<String, String> response = new HashMap<>();
            response.put("concept","testing");
            response.put("format","JSON format");
            response.put("fileContent", base64String);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Create an error response map
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to encode file");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Features for decoding and converting from the string base64 to the original document/image format
    @PostMapping("/decodeDocs-base64")
    public ResponseEntity<byte[]> ClaimDocumentBase64(@RequestBody String base64Request) {
        try{
            //Invoking the service class method that will responsibly handle the logic process
            byte[] fileContent = claimService.decodeClaimDocsBase64(base64Request);

            // Detect MIME type from the byte stream
            String mimeType = claimService.detectMimeType(fileContent);

            // Fallback MIME type if detection fails
            if (mimeType == null) {
                mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            logger.info("Mime Type : {}",mimeType);

            MediaType mediaType = MediaType.parseMediaType(mimeType);
            logger.info("Media Type : {}",mediaType);

            //Prepare Content-Disposition filename dynamically if you want
            //Here we fall back to "document" with appropriate extension based on mimeType
            String extension = claimService.getExtensionFromMimeType(mimeType);
            logger.info("File extension : {}",extension);
            String filename = "converted_document" + (extension != null ? extension : "");

            // Decide Content-Disposition header based on MIME type
            boolean isImage = mimeType.startsWith("image/");

            String contentDisposition = isImage ? "inline; filename=\"" + filename + "\""   // display images inline
                    : "attachment; filename=\"" + filename + "\""; // force download for others

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .contentType(mediaType)
                    .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/update-claim/{claimId}")
    public ResponseEntity<Claim> updateClaimAmount(@RequestParam("claimId") Long claimId,
                                                   @RequestBody Claim claim)
    {

        Claim claimInfo = claimService.updateClaimDetails(claimId,claim);

        try{
            return ResponseEntity.ok(claimInfo);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(claimInfo);
        }
    }

    @DeleteMapping("/delete-claim/{claimId}")
    public ResponseEntity<String> deleteClaimById(@PathVariable("claimId") Long claimId)
    {
        boolean claimDeleted = claimService.deleteClaimById(claimId);
        if (claimDeleted) {
            // Return 204 No Content to indicate successful deletion
            return ResponseEntity.noContent().build();
        } else {
            // Return 404 Not Found if the resource was not found
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @DeleteMapping("/delete-from-storage/{documentPath}")
    public ResponseEntity<String> deleteDocsFromStorage(@PathVariable String documentPath)
    {
        boolean claimDocsDeleted = claimService.deleteClaimDocsStorage(documentPath);
        if (claimDocsDeleted){
            return ResponseEntity.ok("Successfully Deleted " + documentPath);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    ///Backend service communication with customer-service leveraged by Feign Client
    ///{
            @GetMapping("/customer-details/{customerId}")
            public CustomerServiceModel getCustomerDetails(@PathVariable Long customerId) {
                return claimService.getCustomerDetails(customerId);
            }

            @GetMapping("/customer-list")
            public ResponseEntity<List<CustomerServiceModel>> getAllCustomerDetails() {
                List<CustomerServiceModel> customerList = claimService.getAllCustomers();
                return new ResponseEntity<>(customerList,HttpStatus.OK);
            }

            @GetMapping("/customer-init")
            public ResponseEntity<String> getCustomerInit(){
                String customerInit = claimService.customerInit();
                return ResponseEntity.ok().body(customerInit);
            }

            @DeleteMapping("/delete-customer")
            public ResponseEntity<String> deleteCustomerById(@RequestParam("id") Long id){
                boolean isSuccessfulDeleteCustomer = claimService.deleteCustomerById(id);

                if(isSuccessfulDeleteCustomer) {
                    return ResponseEntity.ok("Successfully Removed Customer!");
                }
                else{
                    return ResponseEntity.ok("Unsuccessful in Removing Customer");
                }
            }
    /// }

}
