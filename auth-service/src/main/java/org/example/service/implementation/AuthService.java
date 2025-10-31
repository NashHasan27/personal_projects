package org.example.service.implementation;

import org.example.entity.Authentication;
import org.example.model.AuthenticationRequest;
import org.example.model.AuthenticationResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AuthService {

    AuthenticationResponse submitAuthentication(AuthenticationRequest authenticationRequest);
    List<AuthenticationResponse> getAuthDetails();
    List<Authentication> getAuthDetailsByUsername(String username);
    boolean removeAuthDataById(Long id);
    Authentication updateAuthParticulars(Long id,Authentication authentication);
}
