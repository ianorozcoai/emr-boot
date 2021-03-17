package com.cdsi.emr.medicalreports;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;
import com.cdsi.emr.util.UXMessage;

@Controller
public class MedicalReportsController {
	
	private EMRMedicalCertificateRepository emrMedicalCertificateRepository;
	private PatientRepository patientRepository;
	
	public MedicalReportsController (EMRMedicalCertificateRepository hmoRepository, PatientRepository patientRepository) {
		this.emrMedicalCertificateRepository = hmoRepository;
		this.patientRepository = patientRepository;
	}
	
	@GetMapping("/emrPatientMedicalCertificate/{patientId}")
	public String listAll(Model model, @PathVariable long patientId) {
		List<EMRMedicalCertificate> emrMedicalCertificateList = emrMedicalCertificateRepository.findByPatientIdOrderByDateRequestedDesc(patientId);
		Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		Patient patient = optionalPatient.get();
		model.addAttribute("patient", patient);
		model.addAttribute("emrMedicalCertificateList", emrMedicalCertificateList);
		model.addAttribute("emrMedicalCertificate", new EMRMedicalCertificate());
 
		return "emr/emr_patient_medical_certificate";
	}	
	
	@PostMapping("/emrPatientMedicalCertificate")
	public String saveEMRPatientMedicalCertificate(
			@Valid @ModelAttribute("emrMedicalCertificate") EMRMedicalCertificate emrMedicalCertificate
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			) {
		if (errors.hasErrors()) {
			List<EMRMedicalCertificate> emrMedicalCertificateList = emrMedicalCertificateRepository.findByPatientIdOrderByDateRequestedDesc(emrMedicalCertificate.getPatient().getId());
			Optional<Patient> optionalPatient = patientRepository.findById(emrMedicalCertificate.getPatient().getId());
			Patient patient = optionalPatient.get();
			model.addAttribute("patient", patient);
			model.addAttribute("emrMedicalCertificateList", emrMedicalCertificateList);
			model.addAttribute("emrMedicalCertificate", emrMedicalCertificate);
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
			return "emr/emr_patient_medical_certificate";
		}
		
		emrMedicalCertificateRepository.save(emrMedicalCertificate);
		
		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Medical Certificate added successfully."));
		return "redirect:/emrPatientMedicalCertificate/"+emrMedicalCertificate.getPatient().getId();
	}
}
