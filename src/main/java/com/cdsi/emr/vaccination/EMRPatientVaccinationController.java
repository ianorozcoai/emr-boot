package com.cdsi.emr.vaccination;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.fileupload.FileDTO;
import com.cdsi.emr.fileupload.StorageService;
import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;
import com.cdsi.emr.util.UXMessage;

@Controller
public class EMRPatientVaccinationController {
    private StorageService storageService;
	private EMRPatientVaccinationRepository emrPatientVaccinationRepository;
	private PatientRepository patientRepository;
	private VaccineNameRepository vaccineNameRepository;
	
	public EMRPatientVaccinationController (EMRPatientVaccinationRepository emrPatientVaccinationRepository, PatientRepository patientRepository
			, StorageService storageService
			, VaccineNameRepository vaccineNameRepository
			) {
		this.storageService = storageService;
		this.emrPatientVaccinationRepository = emrPatientVaccinationRepository;
		this.patientRepository = patientRepository;
		this.vaccineNameRepository = vaccineNameRepository;
	}
	
	@GetMapping("/emrpatientVaccination/{patientId}")
	public String listAll(Model model, @PathVariable long patientId) {
		Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		Patient patient = optionalPatient.orElseGet(() -> new Patient());
		model.addAttribute("patient", patient);
		List<EMRPatientVaccination> emrPatientVaccinationList = emrPatientVaccinationRepository.findByPatientId(patientId);
		model.addAttribute("emrPatientVaccinationList", emrPatientVaccinationList);
		EMRPatientVaccination emrPatientVaccination = new EMRPatientVaccination();
		model.addAttribute("emrPatientVaccination", emrPatientVaccination);
		model.addAttribute(vaccineNameRepository.findAll());
		return "emr/emr_patient_vaccination";
	}	
	
	@PostMapping("/emrpatientVaccination")
	public String savePatientVaccination(
			@Valid EMRPatientVaccination emrPatientVaccination
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			, HttpServletRequest request
			) {
		if (errors.hasErrors()) {
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
			Optional<Patient> optionalPatient = patientRepository.findById(emrPatientVaccination.getPatient().getId());
			Patient patient = optionalPatient.get();
			model.addAttribute("patient", patient);
			model.addAttribute("emrPatientVaccination", new EMRPatientVaccination());
			return "emr/emr_patient_vaccination";
		}
		long patientId = emrPatientVaccination.getPatient().getId();
		List<String> vaccineFileUrls = new ArrayList<>();
		MultipartFile[] files = emrPatientVaccination.getVaccineFiles();
		if (files.length > 0 && files[0].getOriginalFilename().isEmpty()) {
				EMRPatientVaccination emrVacc = emrPatientVaccinationRepository.findById(emrPatientVaccination.getId())
						.orElseGet(() -> new EMRPatientVaccination());
				vaccineFileUrls = emrVacc.getVaccineFileUrls();
		} else {
			for (int i = 0; i < files.length; i++) {
				try {
					String fileExt = files[i].getOriginalFilename().substring(files[i].getOriginalFilename().lastIndexOf("."));
					String fileName = "patient_vaccinefile_" + patientId + '_' + i + "_" + System.currentTimeMillis() + fileExt;
					FileDTO fileDTO = storageService.uploadFile(files[i], fileName);
					vaccineFileUrls.add(request.getContextPath() + fileDTO.getDownloadUri());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		emrPatientVaccination.setVaccineFileUrls(vaccineFileUrls);
        Patient patient = patientRepository.findById(patientId).orElseGet(() -> new Patient());
		emrPatientVaccination.setPatient(patient);
		emrPatientVaccinationRepository.save(emrPatientVaccination);
		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Vaccination added successfully."));
		return "redirect:/emrpatientVaccination/" + patientId;
	}
	
}
