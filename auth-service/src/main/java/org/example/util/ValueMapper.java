package org.example.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Authentication;
import org.example.model.AuthenticationRequest;
import org.example.model.AuthenticationResponse;

public class ValueMapper {

    public static Authentication convertToEntity(AuthenticationRequest authenticationRequest){
        Authentication authentication = new Authentication();
        authentication.setUsername(authenticationRequest.getUsername());
        authentication.setPassword(authenticationRequest.getPassword());
        authentication.setAuthMessageTitle(authenticationRequest.getAuthMessageTitle());
        authentication.setAuthMessageContent(authenticationRequest.getAuthMessageContent());
        return authentication;
    }

    public static AuthenticationResponse convertToDTO(Authentication authentication){
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setUsername(authentication.getUsername());
        authenticationResponse.setPassword(authentication.getPassword());
        authenticationResponse.setAuthMessageTitle(authentication.getAuthMessageTitle());
        authenticationResponse.setAuthMessageContent(authentication.getAuthMessageContent());
        return authenticationResponse;
    }

    public static String jsonAsString(Object obj){
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
