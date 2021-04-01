package com.cdsi.emr.procedures;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data 
public class EMRPatientProcedureType{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotBlank(message = " is mandatory.")
	private String procedureTypeName;
		
}
