package com.cdsi.emr.medicalreports;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.cdsi.emr.imaging.EMRPatientImaging;
import com.cdsi.emr.imaging.EMRPatientImagingRepository;
import com.cdsi.emr.labs.EMRPatientLaboratory;
import com.cdsi.emr.labs.EMRPatientLaboratoryRepository;
import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;
import com.cdsi.emr.procedures.EMRPatientProcedure;
import com.cdsi.emr.procedures.EMRPatientProcedureRepository;
import com.cdsi.emr.util.UXMessage;

@Controller
public class MedicalReportsController {
	
	private EMRMedicalCertificateRepository emrMedicalCertificateRepository;
	private PatientRepository patientRepository;
	private EMRPatientLaboratoryRepository emrPatientLaboratoryRepository;
	private EMRPatientImagingRepository emrPatientImagingRepository;
	private EMRPatientProcedureRepository emrPatientProcedureRepository;
	
	public MedicalReportsController (EMRMedicalCertificateRepository hmoRepository, PatientRepository patientRepository
			,EMRPatientLaboratoryRepository emrPatientLaboratoryRepository
			,EMRPatientImagingRepository emrPatientImagingRepository
			,EMRPatientProcedureRepository emrPatientProcedureRepository) {
		this.emrMedicalCertificateRepository = hmoRepository;
		this.patientRepository = patientRepository;
		this.emrPatientLaboratoryRepository = emrPatientLaboratoryRepository;
		this.emrPatientImagingRepository = emrPatientImagingRepository;
		this.emrPatientProcedureRepository = emrPatientProcedureRepository;
	}
	
	@GetMapping("/emrPatientMedicalCertificate/{patientId}")
	public String listAll(Model model, @PathVariable long patientId) {
		List<EMRMedicalCertificate> emrMedicalCertificateList = emrMedicalCertificateRepository.findByPatientIdOrderByDateRequestedDesc(patientId);
		
		Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		Patient patient = optionalPatient.get();
		
		List<EMRPatientLaboratory> emrPatientLaboratoryList = emrPatientLaboratoryRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		List<EMRPatientImaging> emrPatientImagingList = emrPatientImagingRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		List<EMRPatientProcedure> emrPatientProcedureList = emrPatientProcedureRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		
		int totalNewLab = 0;
		int totalNewImaging = 0;
		int totalNewProcedure = 0;
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String today = dateFormatter.format(LocalDateTime.now());

        for(EMRPatientLaboratory obj : emrPatientLaboratoryList) {
        	String dateCreated = dateFormatter.format(obj.getDateCreated());
        	if(dateCreated.equals(today)) {
        		totalNewLab++;
        	}        	
        }
        
        for(EMRPatientImaging obj : emrPatientImagingList) {
        	String dateCreated = dateFormatter.format(obj.getDateCreated());
        	if(dateCreated.equals(today)) {
        		totalNewImaging++;
        	}        	
        }
        
        for(EMRPatientProcedure obj : emrPatientProcedureList) {
        	String dateCreated = dateFormatter.format(obj.getDateCreated());
        	if(dateCreated.equals(today)) {
        		totalNewProcedure++;
        	}        	
        }
        
        patient.setTotalNewLab(totalNewLab);
        patient.setTotalNewImaging(totalNewImaging);
        patient.setTotalNewProcedure(totalNewProcedure);
        
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
