package com.cdsi.emr.consultation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.format.annotation.DateTimeFormat;

import com.cdsi.emr.config.data.Auditable;
import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.personnel.Personnel;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data public class EmrConsultation extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@NotNull(message = " is mandatory.")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate consultationDate = LocalDate.now();
	private String paymentType;
	private String hmoName;
	
	@Column(columnDefinition = "varchar(5) default 'N'")
	private String hmoPaid;
	private BigDecimal consultationFee;
	private String consultationStatus = "ON QUEUE";
	private double temperature;
	private double weight;
	private double height;
	private String bloodPressure;
	private String symptoms;
	private String physical;
	private String intervention;

	@ElementCollection
	private List<EmrConsultationDiagnosis> diagnosis;
	@Transient
	private String diagnosisJSON;
	
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name = "patient_id")
	Patient patient;
	
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name = "doctor_id")
	Personnel personnel;
	
}
