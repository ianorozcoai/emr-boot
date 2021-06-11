package com.cdsi.emr.medication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.cdsi.emr.util.EHRConstants;
import com.cdsi.emr.util.UXMessage;

@Controller
public class EMRPatientMedicationController {
    private EMRPatientMedicationRepository emrPatientMedicationRepository;
    private EMRPatientMedicationItemRepository emrPatientMedicationItemRepository;
	private PatientRepository patientRepository;
	private EMRGenericsLookupRepository emrGenericsLookupRepository;
	private EMRPatientLaboratoryRepository emrPatientLaboratoryRepository;
	private EMRPatientImagingRepository emrPatientImagingRepository;
	private EMRPatientProcedureRepository emrPatientProcedureRepository;

	public EMRPatientMedicationController (EMRPatientMedicationRepository emrPatientMedicationRepository
		, PatientRepository patientRepository
		, EMRPatientMedicationItemRepository emrPatientMedicationItemRepository
	    , EMRGenericsLookupRepository emrGenericsLookupRepository
	    ,EMRPatientLaboratoryRepository emrPatientLaboratoryRepository
		,EMRPatientImagingRepository emrPatientImagingRepository
		,EMRPatientProcedureRepository emrPatientProcedureRepository
			) {
	    this.emrPatientMedicationRepository = emrPatientMedicationRepository;
		this.patientRepository = patientRepository;
		this.emrPatientMedicationItemRepository = emrPatientMedicationItemRepository;
		this.emrGenericsLookupRepository = emrGenericsLookupRepository;
		this.emrPatientLaboratoryRepository = emrPatientLaboratoryRepository;
		this.emrPatientImagingRepository = emrPatientImagingRepository;
		this.emrPatientProcedureRepository = emrPatientProcedureRepository;
	}

	@GetMapping("/emrPatientMedication/{patientId}")
	public String viewPatientMedicationByPatientId(Model model, @PathVariable long patientId) {
		Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		Patient patient = optionalPatient.get();
		
		List<EMRPatientLaboratory> emrPatientLaboratoryList = emrPatientLaboratoryRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		List<EMRPatientImaging> emrPatientImagingList = this.emrPatientImagingRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
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

		List<EMRPatientMedication> emrPatientMedicationList = emrPatientMedicationRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		List<String> emrGenericsLookupList = emrGenericsLookupRepository.findDistinctByGenericName();

		EMRPatientMedicationForm emrPatientMedicationForm = new EMRPatientMedicationForm();
		emrPatientMedicationForm.getEmrPatientMedicationItems().add(new EMRPatientMedicationItem());
		model.addAttribute("emrPatientMedicationList", emrPatientMedicationList);
		model.addAttribute("emrGenericsLookupList", emrGenericsLookupList);
		model.addAttribute("emrPatientMedicationForm", emrPatientMedicationForm);
		model.addAttribute("dosages", EHRConstants.DOSAGE);

		return "emr/emr_patient_medication";
	}

	@PostMapping("/emrpatientMedication")
	@Transactional
	public String savePatientMedication(
			@Valid EMRPatientMedicationForm emrPatientMedicationForm
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			) {
	    
		if (errors.hasErrors()) {
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check Generic and Dosage values. They are mandatory."));
			Optional<Patient> optionalPatient = patientRepository.findById(emrPatientMedicationForm.getEmrPatientMedication().getPatient().getId());
			Patient patient = optionalPatient.get();
			model.addAttribute("patient", patient);

			List<EMRPatientMedication> emrPatientMedicationList = emrPatientMedicationRepository.findByPatientIdOrderByDateCreatedDesc(patient.getId());
			List<String> emrGenericsLookupList = emrGenericsLookupRepository.findDistinctByGenericName();

			model.addAttribute("emrPatientMedicationList", emrPatientMedicationList);
			model.addAttribute("emrGenericsLookupList", emrGenericsLookupList);
			model.addAttribute("emrPatientMedicationForm", emrPatientMedicationForm);
			model.addAttribute("dosages", EHRConstants.DOSAGE);
			model.addAttribute("emrPatientMedicationForm", emrPatientMedicationForm);
			return "emr/emr_patient_medication";
		}

		List<EMRPatientMedicationItem> itemList = new ArrayList<>();
		itemList.addAll(emrPatientMedicationForm.getEmrPatientMedicationItems());
        
        for (EMRPatientMedicationItem medItem : itemList) {
            if (medItem.getGenericName().equalsIgnoreCase("genericName") || medItem.getDosage().equalsIgnoreCase("dosage")) {
                emrPatientMedicationForm.getEmrPatientMedicationItems().remove(medItem);
            }
        }
        
		EMRPatientMedication emrPatientMedication = emrPatientMedicationForm.getEmrPatientMedication();
		emrPatientMedication.setEmrPatientMedicationItems(emrPatientMedicationForm.getEmrPatientMedicationItems());

		for (EMRPatientMedicationItem item : emrPatientMedication.getEmrPatientMedicationItems()) {
		    item.setEmrPatientMedication(emrPatientMedication);
		}

		emrPatientMedicationItemRepository.deleteByEMRPatientMedicationId(emrPatientMedication.getId());

		emrPatientMedicationRepository.save(emrPatientMedication);
		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Medication added successfully."));
		return "redirect:/emrPatientMedication/" + emrPatientMedicationForm.getEmrPatientMedication().getPatient().getId();
	}

}
