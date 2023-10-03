package com.cdsi.emr.therapy;


import java.util.List;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.util.UXMessage;

@Controller
public class EMRPatientTherapyTypeController {
	
    private EMRPatientTherapyTypeRepository emrPatientTherapyTypeRepository;
	
	
	public EMRPatientTherapyTypeController (EMRPatientTherapyTypeRepository emrPatientTherapyTypeRepository) {		
		this.emrPatientTherapyTypeRepository = emrPatientTherapyTypeRepository;		
	}	
	
	@GetMapping("/patientTherapyTypes")
	public String listAllTherapyType(Model model, @AuthenticationPrincipal Personnel doctor) {		

		List<EMRPatientTherapyType> emrPatientTherapyTypeList = emrPatientTherapyTypeRepository.findAllByDoctorId(doctor.getId());
		
		model.addAttribute("emrPatientTherapyTypeList", emrPatientTherapyTypeList);
		model.addAttribute("emrPatientTherapyType", new EMRPatientTherapyType());
		return "emr/emr_therapy_type_list";
	}
	
	
	
	@PostMapping("/patientTherapyType")
	public String savePatientTherapyType(
			@Valid @ModelAttribute("emrPatientTherapyType") EMRPatientTherapyType emrPatientTherapyType
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			, @AuthenticationPrincipal Personnel doctor
			) {
		if (errors.hasErrors()) {
			List<EMRPatientTherapyType> emrPatientTherapyTypeList = emrPatientTherapyTypeRepository.findAllByDoctorId(doctor.getId());
			
			model.addAttribute("emrPatientTherapyTypeList", emrPatientTherapyTypeList);
			model.addAttribute("emrPatientTherapyType", emrPatientTherapyType);
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
			return "emr/emr_therapy_type_list";
		}
		emrPatientTherapyType.setDoctorId(doctor.getId());
		emrPatientTherapyTypeRepository.save(emrPatientTherapyType);
		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Therapy Type successfully saved."));
		return "redirect:/patientTherapyTypes";
	}	
	
	
}
