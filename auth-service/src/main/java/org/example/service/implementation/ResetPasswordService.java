package org.example.service.implementation;

import java.util.List;
import org.example.entity.ResetToken;
import org.example.model.ResetPasswordRequest;

public interface ResetPasswordService {

    String getCustomerEmail(Long id);
    String generateResetPasswordToken();
    ResetToken saveTokentoDB(Long id);
    List<ResetToken> getTokenDetails();
    ResetPasswordRequest submitPasswordRequest(ResetPasswordRequest resetPasswordRequest);
}
