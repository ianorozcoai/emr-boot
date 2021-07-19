package com.cdsi.emr.icd;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Entity
@Data
public class EmrIcd {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotBlank(message = " is mandatory.")
	private String code;
	@NotBlank(message = " is mandatory.")
	private String description;	
	@NotBlank(message = " is mandatory.")
	private String groupDescription;
	
	
}
