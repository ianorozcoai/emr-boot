package com.cdsi.emr.patient;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import com.cdsi.emr.config.data.Auditable;
import com.cdsi.emr.personnel.Personnel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data 
public class Patient extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "doctor_id")
	private Personnel doctor;
	
	@NotBlank(message = " is mandatory.")
	private String firstName;
	
	@NotBlank(message = " is mandatory.")
	private String lastName;
	private String nickName;
	private String gender;
	@NotNull(message="is mandatory")
	@PastOrPresent
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthdate;	
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
	private String mothersname;
	private String fathersname;
	private String mothersoccupation;
	private String fathersoccupation;
	private String mothersmobile;
	private String fathersmobile;
//	private String zipCode;	
	
	private String allergy;
	
	@Transient
	private MultipartFile photoFile;
	
	@Transient
	int totalNewLab;
	@Transient
	int totalNewImaging;
	@Transient
	int totalNewProcedure;
	
	public String getFullName() {
        return getLastName().toUpperCase() + ", " + getFirstName().toUpperCase();
    }
	
	@JsonIgnore
	public Integer getAge() {
		return Period.between(getBirthdate(), LocalDate.now()).getYears();
	}
	
	@JsonIgnore
	public Long getAgeInMonths() {
		return ChronoUnit.MONTHS.between(getBirthdate(), LocalDate.now());
	}
	
	@JsonIgnore
	public String getAgeStr() {
		int age_years = getAge();
		long age_months = getAgeInMonths();
		
		long calculated_year = age_years * 12;
		long ageMonths = age_months - calculated_year;
		
		String ageStr = "";
		
        if(age_years > 1){
        	ageStr = age_years + " years and " + ageMonths + (ageMonths > 1 ? " months" : " month");
        } else if (age_years == 1) {
        	ageStr = "1 year and " + ageMonths + (ageMonths > 1 ? " months" : " month");
        } else {
        	if(ageMonths == 0){
        		ageStr = "Patient is less than a month";
        	} else {
        		ageStr = ageMonths + (ageMonths > 1 ? " months" : " month");
        	}
        	
        }
        
		return ageStr;		
	}
}
