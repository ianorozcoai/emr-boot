package com.cdsi.emr.medication;

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

import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;
import com.cdsi.emr.util.EHRConstants;
import com.cdsi.emr.util.UXMessage;

@Controller
public class EMRPatientMedicationController {
    	private EMRPatientMedicationRepository emrPatientMedicationRepository;
    	private EMRPatientMedicationItemRepository emrPatientMedicationItemRepository;
	private PatientRepository patientRepository;
	private EMRGenericsLookupRepository emrGenericsLookupRepository;

	public EMRPatientMedicationController (EMRPatientMedicationRepository emrPatientMedicationRepository
		, PatientRepository patientRepository
		, EMRPatientMedicationItemRepository emrPatientMedicationItemRepository
	    , EMRGenericsLookupRepository emrGenericsLookupRepository
			) {
	    	this.emrPatientMedicationRepository = emrPatientMedicationRepository;
		this.patientRepository = patientRepository;
		this.emrPatientMedicationItemRepository = emrPatientMedicationItemRepository;
		this.emrGenericsLookupRepository = emrGenericsLookupRepository;
	}

	@GetMapping("/emrPatientMedication/{patientId}")
	public String viewPatientMedicationByPatientId(Model model, @PathVariable long patientId) {
		Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		Patient patient = optionalPatient.get();
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
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
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
