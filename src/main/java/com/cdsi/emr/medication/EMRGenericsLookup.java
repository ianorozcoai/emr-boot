package com.cdsi.emr.medication;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class EMRGenericsLookup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String genericName;
	private String brandName;
}
