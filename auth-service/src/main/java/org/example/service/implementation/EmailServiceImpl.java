package org.example.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmailResetPassword(String email, String token) {
        String resetUrl = "https://yourapp.com/reset-password?token=" + token;
        String body = "Click the link below to reset your password.\n" + resetUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sender@gmail.com");
        message.setTo(email);
        message.setSubject("Password Reset");
        message.setText(body);

        mailSender.send(message);
    }

    public String sendEmailConfirmation(){
        return "Email Successfully Sent!";
    }
}
