package com.cdsi.emr.consultation;

import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data public class EmrConsultationDiagnosis {

	private long id;
	private String code;
	private String description;
	private long patientId;
}
