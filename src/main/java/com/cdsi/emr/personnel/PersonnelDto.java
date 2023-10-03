package com.cdsi.emr.personnel;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data public class PersonnelDto {
    @Positive
    private long id;
    @NotBlank(message = "First name is required.")
    private String firstName;
    @NotBlank(message = "Last name is required.")
    private String lastName;
    @Size(min = 1, max = 1)
    private String gender;
    private String address;
    private String contactNumber;
    @Email(message = "Invalid email.")
    private String email;
    private String status;
    private String userType;
    private int staffCount;
    private long superiorId;
    private String profilePhotoUrl;

    private String credentials;
    private String licenseNumber;
    private String specialization;
    private String ptrNumber;
    private String sNumber;

    private String clinicLogoUrl;

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
