package com.cdsi.emr.imaging;


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
public class EMRPatientImagingTypeController {
	
    private EMRPatientImagingTypeRepository emrPatientImagingTypeRepository;
	
	
	public EMRPatientImagingTypeController (EMRPatientImagingTypeRepository emrPatientImagingTypeRepository) {		
		this.emrPatientImagingTypeRepository = emrPatientImagingTypeRepository;		
	}	
	
	@GetMapping("/patientImagingTypes")
	public String listAllImagingType(Model model) {		

		List<EMRPatientImagingType> emrPatientImagingTypeList = emrPatientImagingTypeRepository.findAll();
		
		model.addAttribute("emrPatientImagingTypeList", emrPatientImagingTypeList);
		model.addAttribute("emrPatientImagingType", new EMRPatientImagingType());
		return "emr/emr_imaging_type_list";
	}
	
	
	
	@PostMapping("/patientImagingType")
	public String savePatientImagingType(
			@Valid @ModelAttribute("emrPatientImagingType") EMRPatientImagingType emrPatientImagingType
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			) {
		if (errors.hasErrors()) {
			List<EMRPatientImagingType> emrPatientImagingTypeList = emrPatientImagingTypeRepository.findAll();
			
			model.addAttribute("emrPatientImagingTypeList", emrPatientImagingTypeList);
			model.addAttribute("emrPatientImagingType", emrPatientImagingType);
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
			return "emr/emr_imaging_type_list";
		}
		emrPatientImagingTypeRepository.save(emrPatientImagingType);
		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Laboratory Type successfully saved."));
		return "redirect:/patientImagingTypes";
	}	
	
	
}
