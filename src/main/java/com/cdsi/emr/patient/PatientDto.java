package com.cdsi.emr.patient;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import lombok.Data;

@Data 
public class PatientDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private LocalDate dateCreated;
	private LocalDate dateUpdated;
	private String updatedBy;
	private String firstName;
	private String lastName;
	private String nickName;
	private String gender;
	private LocalDate birthdate;
	private String birthDateStr;
	private int age;
	private String ageStr;
	private String address;
	private String street;
	private String city;	
	private String province;
	private String contactNumber;
	private String email;
	private String emergencyContactName;
	private String emergencyContactAddress;
	private String emergencyContactNumber;
	private String maritalStatus;
	private String profession;
	private String seniorCitizenNumber;
	private String notes;
	private String patientPhoto;
	private String username;
	private String password;
	private String isActive;
	private Long doctorId;
	private String mothersname;
	private String fathersname;
	private String mothersoccupation;
	private String fathersoccupation;
	private String mothersmobile;
	private String fathersmobile;
	
	public PatientDto() {}
	
	public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
