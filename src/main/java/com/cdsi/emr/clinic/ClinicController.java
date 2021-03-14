package com.cdsi.emr.clinic;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.util.UXMessage;

@Controller
public class ClinicController {
	
	private ClinicRepository clinicRepository;
	
	public ClinicController (ClinicRepository clinicRepository) {
		this.clinicRepository = clinicRepository;
	}
	
	@GetMapping("/emrclinics")
	public String listAll(Model model, Authentication auth) {
		Personnel doctor = (Personnel) auth.getPrincipal();
		
		List<Clinic> clinicList = clinicRepository.findAllByDoctorId(doctor.getId());		
		
		model.addAttribute("doctor", doctor);
		model.addAttribute("clinics", clinicList);
		model.addAttribute("clinic", new Clinic());
		return "emr/emr_clinic_list";
	}	
	
	@PostMapping("/emrclinics")
	@Transactional
	public String saveEMRClinic(
			@Valid Clinic clinic
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			
			) {
		
		if (errors.hasErrors()) {
			List<Clinic> clinicList = clinicRepository.findAll();
			
			model.addAttribute("clinics", clinicList);
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
			return "emr/emr_clinic_list";
		}
		
		clinicRepository.save(clinic);
		
		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Clinic has been added successfully."));
		return "redirect:/emrclinics";
	}
	
	@GetMapping("/emrclinicsdelete/{clinicId}")
	public String deleteEMRClinic(@PathVariable long clinicId
		,final RedirectAttributes redirect) {
	    Optional<Clinic> clinic = clinicRepository.findById(clinicId);
	    clinicRepository.delete(clinic.get());
	    redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Clinic has been deleted."));
		return "redirect:/emrclinics";
	}
}
