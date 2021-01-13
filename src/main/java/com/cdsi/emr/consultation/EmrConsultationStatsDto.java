package com.cdsi.emr.consultation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data 
public class EmrConsultationStatsDto {

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateFrom;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateTo;
	
	private int totalPatients;
	
	private int totalMale;
	
	private int totalFemale;
	
	private int totalHmo;
	
	private int totalCash;
	
	private BigDecimal totalCashAmount;
	
	private List<EmrConsultation> emrConsultations;	
	
			
}
