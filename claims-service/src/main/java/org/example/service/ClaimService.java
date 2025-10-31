package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client.CustomerClient;
import org.example.model.Claim;
import org.example.model.ClaimStatus;
import org.example.model.CustomerServiceModel;
import org.example.model.Document;
import org.example.repository.ClaimRepository;
import org.example.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;


@Service
@AllArgsConstructor //By introducing this annotation will avoid boilerplate code for constructor
@Slf4j
public class ClaimService {

    private static final Logger logger = LoggerFactory.getLogger(ClaimService.class);
    private static final String STORAGE_DIRECTORY = "Claim_Docs/";

    private final ClaimRepository claimRepository;
    private final DocumentRepository documentRepository;
    private final CustomerClient customerClient;
    private final Tika tika;

    ///Communicate and consuming APIs from Customer-Service via FeignClient
    ///{
            public CustomerServiceModel getCustomerDetails(Long customerId) {
                return customerClient.getCustomerById(customerId);
            }

            public List<CustomerServiceModel> getAllCustomers() {
                logger.info("ClaimService:getAllCustomers execution started.");
                return customerClient.getAllCustomers();
            }

            public String customerInit(){
                return customerClient.getCustomerInit();
            }

            public boolean deleteCustomerById(Long Id) {
                try {
                    customerClient.deleteCustById(Id);
                    logger.info("Successfully deleted customer with ID {}", Id);
                    return true;
                } catch (Exception e) {
                    // Log the exception if necessary
                    System.err.println("Error deleting customer with ID " + Id + ": " + e.getMessage());
                    return false;
                }
            }
    ///}

    @Cacheable(value = "claims")
    @Transactional
    public List<Claim> getAllClaims(){
        return claimRepository.findAll();
    }

    @Transactional
    public List<Document> getClaimDocsById(Long claimId){
        return documentRepository.getAllDocsById(claimId);
    }

    @Transactional
    public List<Document> getAllClaimDocs(){
        return documentRepository.getAllClaimDocs();
    }

    @Transactional
    public Claim submitClaim(Claim claim){
        return claimRepository.save(claim);
    }

    /**
     * Decodes and stores files from an array of base64 strings.
     * Uploading the document through this Base64 methodology.
     * @param base64Files List of base64-encoded file strings.
     * @throws IOException if an error occurs during file writing.
     * Sends & Submit into Database.
     */
    public Document submitClaimDocsBase64(Long claimId, List<String> base64Files, String filename) throws Exception {
        int index = 0;

        //Step 1 :Ensure and pre-checking whether the claim ID really exists
        //As consuming claim ID as parameter/variable
        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new Exception("Claim not found"));

        //Step 2: Ensure and pre-checking the storage directory exists
        Path storagePath = Paths.get(STORAGE_DIRECTORY);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        logger.info("ClaimService:submitClaimDocsBase64 execution started for Base64 string base format file/document upload ::");
        Document docBase64 = null;
        for (String base64File : base64Files) {

            byte[] decodedBytes = Base64.getDecoder().decode(base64File); //Step 3: Decoding the base64 string
            String fileName = filename + index; //Optional step (#1): defining the uploaded file name
            Path filePath = storagePath.resolve(fileName); //Optional step (#2): Define the full file path
            Files.write(filePath, decodedBytes, StandardOpenOption.CREATE_NEW); //Step 4: Write the decoded bytes to a file

            //Step 5:Assigning & Set the values to the Document Model Object
            //These values as assigned that will be saved & stored at database level
            docBase64 = new Document();
            docBase64.setDocumentName(fileName);
            docBase64.setDocumentType("base64 format");
            docBase64.setDocumentSize((long) decodedBytes.length); //Retrieving the uploaded document byte size
            docBase64.setDocumentPath(storagePath.toString());
            docBase64.setUploadDate(LocalDateTime.now());
            docBase64.setClaim(claim);

            //Step 6 :Save into DB
            documentRepository.save(docBase64);
        }

        //Step 7:Returning the Object value Document
        return docBase64;
    }

    ///A service method to serve the logic and functionality to
    ///Converting any types of document into String Values (Base64 format)
    ///Taking an uploaded document/file as its parameter as user will upload a real document
    @Transactional
    public String convertDocsToStringBase64(MultipartFile document) throws Exception {

        Long i = 1L;
        Claim claim = claimRepository.findById(i).orElseThrow(() -> new Exception("Claim not found"));

        logger.info("ClaimService:convertDocsToStringBase64 execution started for Base64 string base format file/document upload ::");
        // Read bytes from the uploaded file
        byte[] fileBytes = document.getBytes();

            //Additionally every transaction of converting docs to base64 format
            //Will be submitted to save into DB and update the Document Object
            Document convertedBase64 = new Document();
            convertedBase64.setDocumentName("Converted Docs to Base64 String format");
            convertedBase64.setDocumentType("base64 format");
            convertedBase64.setDocumentSize((long) fileBytes.length); //Retrieving the uploaded document byte size
            convertedBase64.setDocumentPath("base64 format");
            convertedBase64.setUploadDate(LocalDateTime.now());
            convertedBase64.setClaim(claim);

            //Saving & Submitting into DB for tracking purposes
            documentRepository.save(convertedBase64);

        log.info("ClaimService:convertDocsToStringBase64 execution ended.");

        // Encode bytes to Base64 string
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    ///A service method to serve the logic and functionality
    ///Converting the String Values (Base64 format) into the original bytes of document
    @Transactional
    public byte[] decodeClaimDocsBase64(String base64Data) throws IOException{
        try {
            // Remove data URI scheme if present
            if (base64Data.contains(",")) {
                base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
            }
            // Read the Base64 data from the file
            //String base64Data = Files.readString(filePath).trim();
            logger.info("Base64 data decoding executed ");

            // Decode the Base64 data into bytes
            //logger.info("Decoded bytes: " + Arrays.toString(decodedBytes));
            return Base64.getDecoder().decode(base64Data);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64 input: {}", e.getMessage());
            throw new IOException("Failed to decode Base64 data", e);
        }
    }

    ///Implementation in cases where allowing multiple files to be uploaded at a single time
    @Transactional
    public List<Document> submitClaimDocs(Long claimId, List<MultipartFile> documents) throws Exception {
        logger.info("Starting document upload for claim ID: {}", claimId);

        //Step 1 :Ensure and pre-checking whether the claim ID really exists in the DB
        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new Exception("Claim not found"));

        //Step 2 :Ensure and pre-checking the storage directory exists
        Path storagePath = Paths.get(STORAGE_DIRECTORY);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
            logger.info("Storage directory created at: {}", storagePath.toAbsolutePath());
        }

        //Step 3: Prepare list to hold saved Document entities
        List<Document> Documents = new ArrayList<>();

        //Step 4: Loop & Iterate each documents uploaded at once
        for (MultipartFile document : documents) {
            if (document.isEmpty()) {
                logger.warn("Skipped empty file upload attempt for claim ID: {}", claimId);
                continue; // skip empty files instead of throwing exception
            }

            String uniqueFileName = UUID.randomUUID() + "_" + document.getOriginalFilename(); // Generate a unique file name to prevent overwriting
            Path filePath = storagePath.resolve(uniqueFileName);

            ///Writing the file to the storage directory
            ///By handling this logic it will route the blob/large size bytes of data into the file storage system
            ///Ensuring the database only holds the metadata and structured data of the file object
            ///At the same time detaching the big bytes data(BLOB) of the uploaded file from the database to store into file storage
            try {
                //Step 5 :Write the file to the storage directory
                Files.write(filePath, document.getBytes());
            } catch (IOException ioException) {
                logger.error("Error writing file to storage: {}", uniqueFileName, ioException);
                throw new RuntimeException("Failed to store file: " + uniqueFileName, ioException);
            }

            //Step 6 :Assigning & Set the values to the Document Model Object
            Document doc = new Document();
            doc.setDocumentName(document.getOriginalFilename());
            doc.setDocumentType(document.getContentType());
            doc.setDocumentSize(document.getSize());
            doc.setDocumentPath(filePath.toString());
            doc.setUploadDate(LocalDateTime.now());
            doc.setClaim(claim);

            //Step 7 :Save into DB
            Document savedDocs = documentRepository.save(doc);

            //Adding into the list of every populated document processed within the loop
            Documents.add(savedDocs);

            logger.info("Document '{}' uploaded successfully for claim ID: {}", document.getOriginalFilename(), claimId);
        }

        //Step 8: To return the List of documents in order to return as JSON List Object format
        return Documents;
    }

    @Transactional
    public byte[] getClaimDocsContent(String documentPath) throws IOException{
        Path filePath = Paths.get(STORAGE_DIRECTORY, documentPath);
        return Files.readAllBytes(filePath);
    }


    @Transactional
    public Claim updateClaimDetails(Long claimId,Claim claim){
        //Shorthand in handling & checking if the claim exists and assigning it
        //Step 1: Retrieving the claim object based on the claim ID pass from parameter
        Claim existingClaim = claimRepository.findById(claimId)
                .orElseThrow(() -> new EntityNotFoundException("Claim not found with ID: " + claimId));

            //Step 2: Assigning values into the Claim Object
            existingClaim.setClaimAmount(claim.getClaimAmount());
            existingClaim.setPolicyId(claim.getPolicyId());
            existingClaim.setClaimantName(claim.getClaimantName());
            existingClaim.setClaimantContact(claim.getClaimantContact());
            existingClaim.setDateOfClaim(claim.getDateOfClaim());
            existingClaim.setIncidentDate(claim.getIncidentDate());
            existingClaim.setDescriptionOfIncident(claim.getDescriptionOfIncident());
            existingClaim.setClaimStatus(ClaimStatus.SUBMITTED);

        try {
                log.info("ClaimService:updateClaimDetails performing update to database for id {}", claimId);

                //Step 3: Updating at Database level by invoking the update method from the Claim Repository class
                claimRepository.updateClaimInfo(claimId,
                            existingClaim.getClaimAmount(),
                            existingClaim.getPolicyId(),
                            existingClaim.getClaimantName(),
                            existingClaim.getClaimantContact(),
                            existingClaim.getDateOfClaim(),
                            existingClaim.getIncidentDate(),
                            existingClaim.getDescriptionOfIncident(),
                            existingClaim.getClaimStatus());

        } catch (Exception e) {
            // Log the error and throw a custom exception or handle it appropriately
            logger.error("Error updating claim amount for claim ID: {}", claimId, e);
            throw new RuntimeException("Failed to update claim amount.");
        }
        log.info("ClaimService:updateClaimDetails execution ended.");
        return existingClaim;
    }

    @Transactional
    public boolean deleteClaimById(Long claimId) {
        try {
            Claim existingClaim = claimRepository.findById(claimId)
                    .orElseThrow(() -> new EntityNotFoundException("Claim not found with ID: " + claimId));

            List<Document> documents = documentRepository.getAllDocsById(claimId);
            try {
                    //Get the path to the storage directory
                Path storagePath = Paths.get(STORAGE_DIRECTORY);

                    //List all files in the storage directory
                    //Use Files.list to iterate over files in the Claim_Docs directory.
                    //Implementing and using the Lambda Stream package to stream along the files within the directory
                Files.list(storagePath).forEach(filePath -> {
                    try {
                        // Pre-checking the file name contains any document name from the database
                        // If it contains & exists the file from the file storage will be deleted as well
                        for (Document document : documents) {
                            String documentName = document.getDocumentName();
                            if (filePath.getFileName().toString().contains(documentName)) {
                                Files.deleteIfExists(filePath); // Delete the file from storage
                                logger.info("File deleted successfully from file storage: {}", filePath.getFileName());
                            }
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
            // 1.After attempt to remove & delete files from file storage
            // 2.Then proceed to delete and remove from DB

            // Delete documents from the database
            documentRepository.deleteByClaimId(claimId);

            // Delete the claim from the database
            claimRepository.deleteClaimById(claimId);

            logger.info("Successfully deleted claim and associated documents with ID: {}", claimId);
            return true;
        } catch (EntityNotFoundException entityNotFoundException) {
            logger.error("Claim not found with ID: {}", claimId, entityNotFoundException);
            return false;
        } catch (Exception e) {
            logger.error("Error deleting claim and documents with ID: {}", claimId, e);
            return false;
        }
    }

    @Transactional
    public boolean deleteClaimDocsStorage(String documentPath){
        Path filePath = Paths.get(STORAGE_DIRECTORY, documentPath);
        logger.info("Document Path : {}", filePath);
        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                logger.info("File deleted successfully: {}", documentPath);
            } else {
                logger.warn("File not found, nothing to delete: {}", documentPath);
            }
            return deleted;
        } catch (IOException e) {
            logger.error("Error deleting file: {}", documentPath, e);
            throw new RuntimeException("Failed to delete document file: " + documentPath, e);
        }
    }

    public String getExtensionFromMimeType(String mimeType) {
        switch (mimeType) {
            case "application/pdf": return ".pdf";
            case "image/jpeg": return ".jpg";
            case "image/png": return ".png";
            case "application/msword": return ".doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document": return ".docx";
            case "application/vnd.ms-excel": return ".xls";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": return ".xlsx";
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation": return ".pptx";
            case "application/x-tika-ooxml": return ".ooxml";
            // add more as needed
            default: return null;
        }
    }

    public String detectMimeType(byte[] fileContent){
        try (InputStream is = new ByteArrayInputStream(fileContent)) {
            return tika.detect(is);
        } catch (IOException e) {
            // fallback
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }

}
