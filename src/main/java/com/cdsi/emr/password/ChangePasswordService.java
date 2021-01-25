package com.cdsi.emr.password;

public interface ChangePasswordService {
    void changePassword(String username, ChangePasswordRequestDto changePasswordRequest, ChangePasswordType requestType);
    ForgotPasswordToken generateForgotPasswordToken(ForgotPasswordRequestDto forgotPasswordRequestDto);
    void sendGeneratedTokenToUserEmail(ForgotPasswordToken forgotPasswordToken, String appUrl);
}
