package org.example.service.implementation;

import org.example.client.CustomerClient;
import org.example.entity.ResetToken;
import org.example.model.ResetPasswordRequest;
import org.example.repository.ResetTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class ResetPasswordImpl implements ResetPasswordService{

    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordImpl.class);
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    private final CustomerClient customerClient;
    private final ResetTokenRepository resetTokenRepository;

    @Autowired
    public ResetPasswordImpl(CustomerClient customerClient,ResetTokenRepository resetTokenRepository) {
        this.customerClient = customerClient;
        this.resetTokenRepository = resetTokenRepository;
    }

    @Override
    public String getCustomerEmail(Long id) {
        return customerClient.getCustomerEmail(id);
    }

    @Override
    public String generateResetPasswordToken() {
        byte[] randomBytes = new byte[32]; // 256-bit token
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    @Override
    public ResetToken saveTokentoDB(Long id) {
        String token = generateResetPasswordToken();

        ResetToken record = new ResetToken();
        record.setCustomerEmail(getCustomerEmail(id));
        record.setToken(token);
        record.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        resetTokenRepository.save(record);
        return record;
    }

    @Override
    public List<ResetToken> getTokenDetails() {
        return resetTokenRepository.findAll();
    }

    @Override
    public ResetPasswordRequest submitPasswordRequest(ResetPasswordRequest resetPasswordRequest) {
        return null;
    }
}
