package com.cdsi.emr.password;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {
    @NotBlank(message = "Old Password is required.")
    private String oldPassword;
    @NotBlank(message = "New Password is required.")
    private String newPassword;
    @NotBlank(message = "Confirm New Password is required.")
    private String confirmNewPassword;
    private String token;
}
