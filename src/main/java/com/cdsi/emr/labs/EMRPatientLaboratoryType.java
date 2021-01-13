package com.cdsi.emr.labs;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data 
public class EMRPatientLaboratoryType{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotBlank(message = " is mandatory.")
	private String labTypeName;
	
	@NotBlank(message = " is mandatory.")
	private String labTypeNormalValue;
}
