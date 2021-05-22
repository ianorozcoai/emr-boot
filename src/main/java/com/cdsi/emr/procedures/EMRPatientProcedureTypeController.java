package com.cdsi.emr.procedures;


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
public class EMRPatientProcedureTypeController {
	
    private EMRPatientProcedureTypeRepository emrPatientProcedureTypeRepository;
	
	
	public EMRPatientProcedureTypeController (EMRPatientProcedureTypeRepository emrPatientProcedureTypeRepository) {		
		this.emrPatientProcedureTypeRepository = emrPatientProcedureTypeRepository;		
	}	
	
	@GetMapping("/patientProcedureTypes")
	public String listAllProcedureType(Model model, @AuthenticationPrincipal Personnel doctor) {		

		List<EMRPatientProcedureType> emrPatientProcedureTypeList = emrPatientProcedureTypeRepository.findAllByDoctorId(doctor.getId());
		
		model.addAttribute("emrPatientProcedureTypeList", emrPatientProcedureTypeList);
		model.addAttribute("emrPatientProcedureType", new EMRPatientProcedureType());
		return "emr/emr_procedure_type_list";
	}
	
	
	
	@PostMapping("/patientProcedureType")
	public String savePatientProcedureType(
			@Valid @ModelAttribute("emrPatientProcedureType") EMRPatientProcedureType emrPatientProcedureType
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			, @AuthenticationPrincipal Personnel doctor
			) {
		if (errors.hasErrors()) {
			List<EMRPatientProcedureType> emrPatientProcedureTypeList = emrPatientProcedureTypeRepository.findAllByDoctorId(doctor.getId());
			
			model.addAttribute("emrPatientProcedureTypeList", emrPatientProcedureTypeList);
			model.addAttribute("emrPatientProcedureType", emrPatientProcedureType);
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
			return "emr/emr_Procedure_type_list";
		}
		emrPatientProcedureType.setDoctorId(doctor.getId());
		emrPatientProcedureTypeRepository.save(emrPatientProcedureType);
		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Procedure Type successfully saved."));
		return "redirect:/patientProcedureTypes";
	}	
	
	
}
