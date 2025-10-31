package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.Authentication;
import org.example.entity.ResetToken;
import org.example.model.AuthenticationRequest;
import org.example.model.AuthenticationResponse;
import org.example.service.implementation.AuthService;
import org.example.service.implementation.EmailService;
import org.example.service.implementation.ResetPasswordService;
import org.example.util.ValueMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final ResetPasswordService resetPasswordService;
    private final EmailService emailService;

    public AuthController(AuthService authService,ResetPasswordService resetPasswordService,EmailService emailService){
        this.authService = authService;
        this.resetPasswordService = resetPasswordService;
        this.emailService = emailService;
    }

    @GetMapping("/authenticate")
    public ResponseEntity<String> authentication(){
        return ResponseEntity.ok("Authenticated Successfully");
    }

    @PostMapping("/submit-auth")
    public ResponseEntity<AuthenticationResponse> submitAuth(@RequestBody @Valid AuthenticationRequest authenticationRequest){
        AuthenticationResponse authResponse = authService.submitAuthentication(authenticationRequest);
        log.info("Successfully submitted authentication details");

        return new ResponseEntity<>(authResponse,HttpStatus.CREATED);
    }

    @GetMapping("/auth-list")
    public ResponseEntity<List<AuthenticationResponse>> AuthListing(){
        List<AuthenticationResponse> authenticationList = authService.getAuthDetails();

        if(authenticationList.isEmpty()){
            //String noDataMessage = "Auth Data is empty and have no data";
            log.warn("AuthController::getAuthDetails is empty and have no data {}", ValueMapper.jsonAsString(authenticationList));
            return new ResponseEntity<>(authenticationList,HttpStatus.NO_CONTENT);
        }
        else {
            log.info("AuthController::getAuthDetails response {}", ValueMapper.jsonAsString(authenticationList));
            return new ResponseEntity<>(authenticationList, HttpStatus.OK);
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Authentication>> getAuthDetailsByUsername(@PathVariable String username){
        // Retrieving the lists of Authentication details by username
        List<Authentication> authDetailsByUsername = authService.getAuthDetailsByUsername(username);
        log.info("AuthController::getAuthDetailsByUsername response {}", authDetailsByUsername);
        return new ResponseEntity<>(authDetailsByUsername,HttpStatus.OK);
    }

    @DeleteMapping("/remove-auth")
    public ResponseEntity<String> deleteAuthDataById(@RequestParam("id") Long id){
        boolean authRemoveData = authService.removeAuthDataById(id);

        if (authRemoveData) {
            log.info("AuthController::deleteAuthDataById executed successfully for id {}", id);
            return ResponseEntity.ok("Successfully Deleted Authentication Credentials");
        }

        else {
            log.warn("AuthController::deleteAuthDataById failed for id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to Delete Auth Data: ID not found");
        }
    }

    @PutMapping("/update-authMessages")
    public ResponseEntity<Authentication> updateAuthMessages(@RequestParam("id") Long id,
                                                             @RequestBody Authentication authentication)
    {
        Authentication updatedAuthParticulars = authService.updateAuthParticulars(id,authentication);
        return new ResponseEntity<>(updatedAuthParticulars,HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> requestReset(@RequestParam("id") Long id){
        ResetToken resetToken = resetPasswordService.saveTokentoDB(id);
        return ResponseEntity.ok("If the email is registered, you'll get a reset link");
    }

    @GetMapping("/token-details")
    public ResponseEntity<List<ResetToken>> getTokenDetails(){
        List<ResetToken> resetTokenList = resetPasswordService.getTokenDetails();
        return new ResponseEntity<>(resetTokenList,HttpStatus.OK);
    }

    @GetMapping("/customer-email/{id}")
    public ResponseEntity<String> getCustomerEmail(@PathVariable Long id){
        String customerEmail = resetPasswordService.getCustomerEmail(id);
        return new ResponseEntity<>(customerEmail,HttpStatus.OK);
    }

    @PostMapping("/email-resetPassword")
    public ResponseEntity<String> sendEmailResetPassword(){
        String token = "@#4321##";
        String email = "nashminhasan27@gmail.com";
        emailService.sendEmailResetPassword(email,token);
        return ResponseEntity.ok("Email Successfully Sent!");
    }

    @GetMapping("/email-verification")
    public ResponseEntity<String> emailVerification(){
        return ResponseEntity.ok("Email Sent Successfully!");
    }

}
