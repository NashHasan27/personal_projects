package org.example.service.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Authentication;
import org.example.model.AuthenticationRequest;
import org.example.model.AuthenticationResponse;
import org.example.repository.AuthRepository;
import org.example.util.ValueMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;

    @Override
    public AuthenticationResponse submitAuthentication(AuthenticationRequest authenticationRequest) {
        AuthenticationResponse authenticationResponse = null;
        try{
            //Printing the logs to initiate the start of the program
            log.info("AuthServiceImpl:submitAuthentication execution started");

            //Step 1:Mapping and converting the Request DTO into Entity(Auth Table)
            Authentication authentication = ValueMapper.convertToEntity(authenticationRequest);
            log.info("AuthServiceImpl:submitAuthentication request parameters {}", ValueMapper.jsonAsString(authenticationRequest));

            //Step 2:Saving the requests into the database
            log.info("AuthServiceImpl: performing database insert process into AUTH table");
            Authentication authData = authRepository.save(authentication);

            //Step 3:Mapping and converting the authentication response from Database into DTO to be consumed into authentication response
            authenticationResponse = ValueMapper.convertToDTO(authData);
            log.info("AuthServiceImpl:submitAuthentication received response from Database {}", ValueMapper.jsonAsString(authenticationResponse));

        } catch (Exception ex) {
            log.error("Exception occurred while persisting product to database , Exception message {}", ex.getMessage());
        }
        //Printing the logs to show end of the program
        log.info("AuthServiceImpl:submitAuthentication execution ended.");

        //Step 4:Finally,to return the authentication response
        return authenticationResponse;
    }

    @Override
    public List<AuthenticationResponse> getAuthDetails() {
        List<AuthenticationResponse> authenticationResponseList = null;
        try {
            log.info("AuthServiceImpl:getAuthDetails execution started.");

            //Step 1: To retrieve and find the submitted details/data from the database table
            List<Authentication> authenticationList = authRepository.findAll();

            //Step 2: To stream and collect the data that has been submitted into database using stream() method
            if (!authenticationList.isEmpty()) {
                authenticationResponseList = authenticationList.stream()
                        .map(ValueMapper::convertToDTO)
                        .collect(Collectors.toList());

                log.info("Retrieving from database");
            } else {
                //Conditional Step: Else it will return a null/empty list value
                authenticationResponseList = Collections.emptyList();
                log.info("Empty List retrieve from database");
            }
            log.info("AuthServiceImpl:getAuthDetails retrieving authentication details from database  {}", ValueMapper.jsonAsString(authenticationResponseList));

        } catch (Exception ex) {
            log.error("Exception occurred while retrieving products from database , Exception message {}", ex.getMessage());
            //throw new ProductServiceBusinessException("Exception occurred while fetch all products from Database");
        }
        log.info("AuthServiceImpl:getAuthDetails execution ended.");

        //Step 3: Return the retrieved value extracted from database
        return authenticationResponseList;
    }

    @Override
    public List<Authentication> getAuthDetailsByUsername(String username) {
        List<Authentication> authDetailsByUsername = List.of();
        try{
            log.info("AuthServiceImpl:getAuthDetailsByUsername execution started.");
            authDetailsByUsername = authRepository.getAuthDatabyUsername(username);
        }
        catch (Exception ex){
            log.error("Exception occurred while retrieving authentication data from database , Exception message {}", ex.getMessage());
        }
        return authDetailsByUsername;
    }

    @Override
    @Transactional
    public boolean removeAuthDataById(Long id) {

        if (id == null) {
            log.warn("Attempted to delete Authentication with null ID.");
            return false;
        }
        try {
            //Invoking the delete method & native queries declared in the Auth Repository class
            authRepository.deleteAuthDataById(id);

            log.info("Successfully deleted Authentication with ID: {}", id);

            return true;
        }
        catch (EmptyResultDataAccessException e) {
            log.error("Failed to delete Authentication with ID: {}. Entity not found.", id);
            return false;
        }
        catch (Exception e) {
            log.error("An error occurred while deleting Authentication with ID: {}", id, e);
            return false;
        }

    }

    @Transactional
    @Override
    public Authentication updateAuthParticulars(Long id, Authentication authentication) {
        // Logging purposes to add more context of the implementation logic
        log.info("AuthServiceImpl:updateAuthMessages execution started for ID: {}", id);

        // Validate input parameters
        if (id == null || authentication == null) {
            log.error("Invalid input parameters: ID or Authentication is null.");
            throw new IllegalArgumentException("ID and Authentication must not be null.");
        }

        try {

            //Assigning values based on the given request from the end user/FE system
            String authMessageTitle = authentication.getAuthMessageTitle();
            String authMessageContent = authentication.getAuthMessageContent();

            // Validate authentication message details
            if (authMessageTitle == null || authMessageContent == null) {
                log.error("Authentication message title or content is null.");
                throw new IllegalArgumentException("Authentication message title and content must not be null.");
            }

            // Performing the update operation and command by invoking the service method handled in repository class
            authRepository.updateAuthInfo(id, authMessageTitle, authMessageContent);

            // Retrieve and return the updated Authentication object
            Authentication updatedAuthentication = authRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Authentication message not found for ID: " + id));

            // Logging for additional context and information
            log.info("AuthServiceImpl:updateAuthMessages execution ended successfully for ID: {}", id);
            return updatedAuthentication;

        } catch (Exception e) {
            log.error("Error occurred while updating authentication messages for ID: {}", id, e);
            throw e; // Re-throw the exception to ensure transactional rollback
        }
    }

}
