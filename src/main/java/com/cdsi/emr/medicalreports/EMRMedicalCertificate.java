package com.cdsi.emr.medicalreports;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.cdsi.emr.patient.Patient;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Data
public class EMRMedicalCertificate {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull(message = " is mandatory.")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateRequested = LocalDateTime.now();
		
	private String history;
	@NotNull(message = " is mandatory.")
	@NotBlank(message = " is mandatory.")
	private String diagnosis;
	private String recommendation;	
	private String medication;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "patient_id")
	Patient patient;
	
}
