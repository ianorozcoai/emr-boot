package com.cdsi.emr.medication;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
public class EMRPatientMedicationItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotBlank(message = " is mandatory.")
	private String genericName;
	
	@NotBlank(message = " is mandatory.")
	private String brandName;
	
	@NotBlank(message = " is mandatory.")
	private String dosage;
	
	@NotBlank(message = " is mandatory.")
	private String remarks;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "emrpatient_medication_id")
	@ToString.Exclude
	private EMRPatientMedication emrPatientMedication;
}
