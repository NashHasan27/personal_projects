package org.example.service.implementation;

public interface EmailService {

    void sendEmailResetPassword(String email,String token);
}
