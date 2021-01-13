package com.cdsi.emr.labs;


import java.util.List;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.util.UXMessage;

@Controller
public class EMRPatientLaboratoryTypeController {
	
    private EMRPatientLaboratoryTypeRepository emrPatientLaboratoryTypeRepository;
	
	
	public EMRPatientLaboratoryTypeController (EMRPatientLaboratoryTypeRepository emrPatientLaboratoryTypeRepository) {		
		this.emrPatientLaboratoryTypeRepository = emrPatientLaboratoryTypeRepository;		
	}	
	
	@GetMapping("/patientLaboratoryTypes")
	public String listAllLaboratoryType(Model model) {		

		List<EMRPatientLaboratoryType> emrPatientLaboratoryTypeList = emrPatientLaboratoryTypeRepository.findAll();
		
		model.addAttribute("emrPatientLaboratoryTypeList", emrPatientLaboratoryTypeList);
		model.addAttribute("emrPatientLaboratoryType", new EMRPatientLaboratoryType());
		return "emr/emr_laboratory_type_list";
	}
	
	
	
	@PostMapping("/patientLaboratoryType")
	public String savePatientLaboratoryType(
			@Valid @ModelAttribute("emrPatientLaboratoryType") EMRPatientLaboratoryType emrPatientLaboratoryType
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			) {
		if (errors.hasErrors()) {
			List<EMRPatientLaboratoryType> emrPatientLaboratoryTypeList = emrPatientLaboratoryTypeRepository.findAll();
			
			model.addAttribute("emrPatientLaboratoryTypeList", emrPatientLaboratoryTypeList);
			model.addAttribute("emrPatientLaboratoryType", emrPatientLaboratoryType);
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
			return "emr/emr_laboratory_type_list";
		}
		emrPatientLaboratoryTypeRepository.save(emrPatientLaboratoryType);
		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Laboratory Type successfully saved."));
		return "redirect:/patientLaboratoryTypes";
	}	
	
	
}
