package com.cdsi.emr.dashboard;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import com.cdsi.emr.consultation.EmrConsultation;
import lombok.Data;

@Data 
public class EmrDashboardDto {

	private Date today = Date.valueOf(LocalDate.now());
	
	private List<EmrConsultation> emrConsultations;
	
	private int totalToday;
	
	private int totalServed;
	
	private int totalCancelled;
			
}
