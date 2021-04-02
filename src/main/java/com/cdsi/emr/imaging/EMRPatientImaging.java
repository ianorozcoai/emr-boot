package com.cdsi.emr.imaging;

import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import com.cdsi.emr.config.data.Auditable;
import com.cdsi.emr.patient.Patient;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data public class EMRPatientImaging extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull(message = " is mandatory.")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime dateCreated = LocalDateTime.now();
	
	private String remarks;
		
	@ElementCollection
	private List<String> imgFileUrls;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "patient_id")
	Patient patient;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "imaging_type_id")
	@NotNull(message = " is mandatory.")
	EMRPatientImagingType emrPatientImagingType;

	@Transient
	private MultipartFile[] imgFiles;
}
