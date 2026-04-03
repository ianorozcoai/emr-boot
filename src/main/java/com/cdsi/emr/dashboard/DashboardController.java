package com.cdsi.emr.dashboard;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.util.UXMessage;
import com.cdsi.emr.consultation.EmrConsultation;
import com.cdsi.emr.consultation.EmrConsultationRepository;
import com.cdsi.emr.personnel.Personnel;

@Controller
public class DashboardController {
	
	private static final String CANCELLED = "CANCELLED";

    private EmrConsultationRepository emrConsultationRepository;

    public DashboardController(EmrConsultationRepository emrConsultationRepository) {

        this.emrConsultationRepository = emrConsultationRepository;

    }
    
    @PostMapping("/cancelEmrConsulation")
    public String cancelEmrConsulation(
            Long consultationId
            ,final RedirectAttributes redirect
            ){
        Optional<EmrConsultation> oEmrConsultation = this.emrConsultationRepository.findById(consultationId);
        oEmrConsultation.ifPresent(emrConsultation -> {
            emrConsultation.setConsultationStatus(CANCELLED);
            //Temporary Fix
    		//emrConsultation.setConsultationDate(emrConsultation.getConsultationDate().plusDays(1));
            this.emrConsultationRepository.saveAndFlush(emrConsultation);
        });
        redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Consultation Successfully Cancelled."));
        return "redirect:/emrdashboard";
    }

    @GetMapping({"/","/dashboard","/emrdashboard"})
    public String dashboard(Model model, Authentication auth) {
        Personnel loggedUser = (Personnel) auth.getPrincipal();

        //        List<EmrConsultation> emrConsultations = this.emrConsultationRepository.findAllByConsultationDateAndPersonnelId(LocalDate.now().plusDays(1), loggedUser.getId());

        List<EmrConsultation> emrConsultations = new ArrayList<>();

//        List<EmrConsultation> emrConsultationProcessed = this.emrConsultationRepository
//                .findAllByConsultationDateAndPersonnelIdAndConsultationStatusOrderByConsultationDateDesc(LocalDate.now().plusDays(1), loggedUser.getId(), "PROCESSED");
//
//        List<EmrConsultation> emrConsultationPending = this.emrConsultationRepository
//                .findAllByConsultationDateAndPersonnelIdAndConsultationStatusOrderByConsultationDateDesc(LocalDate.now().plusDays(1), loggedUser.getId(), "ON QUEUE");
//
//        List<EmrConsultation> emrConsultationCancelled = this.emrConsultationRepository
//                .findAllByConsultationDateAndPersonnelIdAndConsultationStatusOrderByConsultationDateDesc(LocalDate.now().plusDays(1), loggedUser.getId(), "CANCELLED");
        
        List<EmrConsultation> emrConsultationProcessed = this.emrConsultationRepository
                .findAllByConsultationDateAndPersonnelIdAndConsultationStatusOrderByConsultationDateDesc(LocalDate.now(), loggedUser.getId(), "PROCESSED");

        List<EmrConsultation> emrConsultationPending = this.emrConsultationRepository
                .findAllByConsultationDateAndPersonnelIdAndConsultationStatusOrderByConsultationDateDesc(LocalDate.now(), loggedUser.getId(), "ON QUEUE");

        List<EmrConsultation> emrConsultationCancelled = this.emrConsultationRepository
                .findAllByConsultationDateAndPersonnelIdAndConsultationStatusOrderByConsultationDateDesc(LocalDate.now(), loggedUser.getId(), "CANCELLED");

        emrConsultations.addAll(emrConsultationPending);
        emrConsultations.addAll(emrConsultationProcessed);
        emrConsultations.addAll(emrConsultationCancelled);

        int cancelled = 0;
        int processed = 0;
        int todaysPatient = 0;
        int onqueue = 0;

        for(EmrConsultation emrConsultation : emrConsultations) {
            if("CANCELLED".equals(emrConsultation.getConsultationStatus())) {
                cancelled++;
            } else if("PROCESSED".equals(emrConsultation.getConsultationStatus())) {
                processed++;
            } else if("ON QUEUE".equals(emrConsultation.getConsultationStatus())) {
                onqueue++;
            }
            todaysPatient++;
        }


        EmrDashboardDto emrDashboardDto = new EmrDashboardDto();

        emrDashboardDto.setEmrConsultations(emrConsultations);
        emrDashboardDto.setTotalCancelled(cancelled);
        emrDashboardDto.setTotalServed(processed);
        emrDashboardDto.setTotalToday(todaysPatient);
        emrDashboardDto.setTotalOnqueue(onqueue);
        emrDashboardDto.setSubscriptionValidUntil(Date.valueOf(loggedUser.getEndDate()));

        model.addAttribute("emrDashboardDto", emrDashboardDto);
        return "emr/emr_dashboard";
    }

    @GetMapping("/exportdata")
    public String exportData(Model model) {
        return "emr/exportdata";
    }
}
