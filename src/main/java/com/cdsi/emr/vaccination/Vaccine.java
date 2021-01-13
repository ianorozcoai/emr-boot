package com.cdsi.emr.vaccination;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor @NoArgsConstructor
@Data public class Vaccine {
	
	@NotBlank(message = " is mandatory.")
	@Column(nullable = false)
	private String vaccineName;
	
	@NotBlank(message = " is mandatory.")
	@Column(nullable = false)
	private String remarks;
	
	private String vaccineFileUrls;
	
	@Transient
	private MultipartFile[] vaccineFiles;
}
