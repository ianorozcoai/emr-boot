package com.cdsi.emr.patient;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import com.cdsi.emr.config.data.Auditable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data @EqualsAndHashCode(callSuper = true)
public class PatientDoctorAssignment extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Positive
	private long patientId;
	@Positive
	private long doctorId;
	@Positive
	private long admissionId;
	@NotBlank
	private String admissionType;
}
