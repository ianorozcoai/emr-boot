package com.cdsi.emr.dashboard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.cdsi.emr.dashboard.EmrDashboardDto;
import com.cdsi.emr.consultation.EmrConsultation;
import com.cdsi.emr.consultation.EmrConsultationRepository;
import com.cdsi.emr.personnel.Personnel;

@Controller
public class DashboardController {
	
	private EmrConsultationRepository emrConsultationRepository;
	
	public DashboardController(EmrConsultationRepository emrConsultationRepository) {
		
		this.emrConsultationRepository = emrConsultationRepository;
		
	}

	@GetMapping({"/","/dashboard"})
	public String dashboard(Model model, Authentication auth) {
		Personnel loggedUser = (Personnel) auth.getPrincipal();	
		
		List<EmrConsultation> emrConsultations = this.emrConsultationRepository.findAllByConsultationDateAndPersonnelId(LocalDate.now().plusDays(1), loggedUser.getId());
		
		int cancelled = 0;
		int processed = 0;
		int todaysPatient = 0;
		
		for(EmrConsultation emrConsultation : emrConsultations) {
			if("CANCELLED".equals(emrConsultation.getConsultationStatus())) {
				cancelled++;
			} else if("PROCESSED".equals(emrConsultation.getConsultationStatus())) {
				processed++;
			}			
			todaysPatient++;
		}
		
		EmrDashboardDto emrDashboardDto = new EmrDashboardDto();
		
		emrDashboardDto.setEmrConsultations(emrConsultations);
		emrDashboardDto.setTotalCancelled(cancelled);
		emrDashboardDto.setTotalServed(processed);
		emrDashboardDto.setTotalToday(todaysPatient);
		
		model.addAttribute("emrDashboardDto", emrDashboardDto);
		return "emr/emr_dashboard";
	}
}
