package com.cdsi.emr.patient;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.consultation.EmrConsultation;
import com.cdsi.emr.consultation.EmrConsultationDiagnosis;
import com.cdsi.emr.consultation.EmrConsultationRepository;
import com.cdsi.emr.fileupload.FileDTO;
import com.cdsi.emr.fileupload.StorageService;
import com.cdsi.emr.hmo.HmoRepository;
import com.cdsi.emr.medication.EMRGenericsLookupRepository;
import com.cdsi.emr.medication.EMRPatientMedication;
import com.cdsi.emr.medication.EMRPatientMedicationForm;
import com.cdsi.emr.medication.EMRPatientMedicationItem;
import com.cdsi.emr.medication.EMRPatientMedicationRepository;
import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.util.EHRConstants;
import com.cdsi.emr.util.UXMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PatientController {
	
	private PatientRepository patientRepository;
	private HmoRepository hmoRepository;
	private EmrConsultationRepository emrConsultationRepository;
	private EMRPatientMedicationRepository emrPatientMedicationRepository;
	private EMRGenericsLookupRepository emrGenericsLookupRepository;
	private PatientMedicalHistoryRepository patientMedicalHistoryRepository;
	private ObjectMapper mapper;
	private StorageService storageService;
	
	public PatientController (
		PatientRepository patientRepository
		,HmoRepository hmoRepository
		,EmrConsultationRepository emrConsultationRepository
		,EMRPatientMedicationRepository emrPatientMedicationRepository
		,EMRGenericsLookupRepository emrGenericsLookupRepository
		,PatientMedicalHistoryRepository patientMedicalHistoryRepository
		,ObjectMapper mapper
		,StorageService storageService
	) {
		this.patientRepository = patientRepository;
		this.hmoRepository = hmoRepository;
		this.emrConsultationRepository = emrConsultationRepository;
		this.emrPatientMedicationRepository = emrPatientMedicationRepository;
		this.emrGenericsLookupRepository = emrGenericsLookupRepository;
		this.patientMedicalHistoryRepository = patientMedicalHistoryRepository;
		this.mapper = mapper;
		this.storageService = storageService;
	}
	
	@GetMapping("/adminpatients")
	public String listAll(Model model) {
		Iterable<Patient> patients = patientRepository.findAll();
		model.addAttribute("patients", patients);
		return "admin/patient_list";
	}
	
	@GetMapping("/emrpatients")
	public String listAllPatientsByDoctorId(Model model, Authentication auth) {
		Personnel doctor = (Personnel) auth.getPrincipal();
		Iterable<Patient> patients = patientRepository.findAllByDoctorId(doctor.getId());
		Patient patient = new Patient();
		patient.setDoctor(doctor);
		patient.setIsActive("A");
		model.addAttribute("patient", patient);
		model.addAttribute("patients", patients);
		return "emr/emr_patient_list";
	}
	
	@GetMapping("/emrpatientrecord/{patientId}")
	public String viewPatientRecordByPatientId(Model model, @PathVariable long patientId) {
		Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		Patient patient = optionalPatient.get();
		model.addAttribute("patient", patient);
		EmrConsultation emrConsultation = new EmrConsultation();
		emrConsultation.setPatient(patient);
		model.addAttribute("emrConsultation", emrConsultation);
		List<EmrConsultation> emrConsultations = this.emrConsultationRepository.findAllByPatientId(patientId);
		Consumer<EmrConsultation> fetchDiagnosis = ec -> {
			List<EmrConsultationDiagnosis> diagnosis = ec.getDiagnosis();
			try {
				ec.setDiagnosisJSON(mapper.writeValueAsString(diagnosis));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		};

		emrConsultations.forEach(fetchDiagnosis);
		model.addAttribute("emrConsultations", emrConsultations);
		model.addAttribute("hmos", hmoRepository.findAll());

		model.addAttribute("selectedConsultationId", 0);
		
		return "emr/emr_patient_record";
	}
	
	@GetMapping("/emrpatientrecord/{patientId}/{consultationId}")
    public String viewPatientRecordFromDashboard(Model model
            , @PathVariable long patientId
            , @PathVariable long consultationId) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        Patient patient = optionalPatient.get();
        model.addAttribute("patient", patient);
        EmrConsultation emrConsultation = new EmrConsultation();
        emrConsultation.setPatient(patient);
        model.addAttribute("emrConsultation", emrConsultation);
        List<EmrConsultation> emrConsultations = this.emrConsultationRepository.findAllByPatientId(patientId);
        Consumer<EmrConsultation> fetchDiagnosis = ec -> {
            List<EmrConsultationDiagnosis> diagnosis = ec.getDiagnosis();
            try {
                ec.setDiagnosisJSON(mapper.writeValueAsString(diagnosis));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        };
        
        List<EMRPatientMedication> emrPatientMedicationList = emrPatientMedicationRepository.findByPatientId(patientId);
        List<String> emrGenericsLookupList = emrGenericsLookupRepository.findDistinctByGenericName();
        
        emrConsultations.forEach(fetchDiagnosis);
        model.addAttribute("emrConsultations", emrConsultations);
        model.addAttribute("hmos", hmoRepository.findAll());
        
        EMRPatientMedicationForm emrPatientMedicationForm = new EMRPatientMedicationForm();
        emrPatientMedicationForm.getEmrPatientMedicationItems().add(new EMRPatientMedicationItem());
        model.addAttribute("emrPatientMedicationList", emrPatientMedicationList);
        model.addAttribute("emrGenericsLookupList", emrGenericsLookupList);
        model.addAttribute("emrPatientMedicationForm", emrPatientMedicationForm);
        model.addAttribute("dosages", EHRConstants.DOSAGE);
        
        model.addAttribute("selectedConsultationId", consultationId);
        
        return "emr/emr_patient_record";
    }
	
	@GetMapping("/emrpatientProfile/{patientId}")
	public String viewPatientProfileByPatientId(Model model, @PathVariable long patientId) {
		Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		Patient patient = optionalPatient.get();
		
		List<PatientMedicalHistory> patientMedicalHistoryList = patientMedicalHistoryRepository.findByPatientId(patientId);
		model.addAttribute("patientMedicalHistory", !patientMedicalHistoryList.isEmpty() ? patientMedicalHistoryList.get(0) : new PatientMedicalHistory());
		model.addAttribute("patient", patient);
		EmrConsultation emrConsultation = new EmrConsultation();
		emrConsultation.setPatient(patient);
		return "emr/emr_patient_profile";
	}
	
	@GetMapping({"/view_patient/{patientId}","/view_patient_read_only/{patientId}"})
	public String viewPatientRecord(Model model, @PathVariable long patientId
			, HttpServletRequest request
			) {
		Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		List<PatientMedicalHistory> patientMedicalHistoryList = patientMedicalHistoryRepository.findByPatientId(patientId);
		model.addAttribute("patient", optionalPatient.get());
		model.addAttribute("patientMedicalHistory", !patientMedicalHistoryList.isEmpty() ? patientMedicalHistoryList.get(0) : new PatientMedicalHistory());
		if (request.getServletPath().startsWith("/view_patient_read_only")) {
			model.addAttribute("isReadyOnly", "Y");
			//return "admin/patient_profile_read_only";
		} else {
			model.addAttribute("isReadyOnly", "N");
		}
		
		
		return "admin/patient_profile";
	}
	
	@PostMapping({"/patientMedicalHistory", "/emrPatientMedicalHistory"})
	public String savePatientMedicalHistory(
			@Valid PatientMedicalHistory patientMedicalHistory
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			,HttpServletRequest request
			) {
	    
	      
		if (errors.hasErrors()) {
			Optional<Patient> optionalPatient = patientRepository.findById(patientMedicalHistory.getPatient().getId());
			model.addAttribute("patient", optionalPatient.get());
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
			
			if (request.getServletPath().equalsIgnoreCase("/emrPatientMedicalHistory")) {
				return "emr/emr_patient_profile";
			} else {
				return "admin/patient_profile";
			}
			
		}
		
		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Patient Medical History has been successfully saved."));
		
		patientMedicalHistoryRepository.save(patientMedicalHistory);
		
		if (request.getServletPath().equalsIgnoreCase("/emrPatientMedicalHistory")) {
			return "redirect:/emrpatientProfile/"+patientMedicalHistory.getPatient().getId();
		} else {
			return "redirect:/view_patient/"+patientMedicalHistory.getPatient().getId();
		}
		
		
	}
	
	@PostMapping({"/adminpatients", "/emrpatients", "/emrpatientprofile"})
	public String savePatient(
			@Valid Patient patient
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			,HttpServletRequest request
			) {
	    
	    String uxMessageText = "Patient added successfully.";
	    String uxMessagePatientExists = "This patient already exists. This will create a new patient record.";
	    
	    if (patient.getId() > 0) {
	    	uxMessageText = "Patient profile edited successfully.";
	    } 
		if (errors.hasErrors()) {
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
			model.addAttribute("patients", patientRepository.findAll());
			model.addAttribute("hmos", hmoRepository.findAll());
			if (request.getServletPath().equalsIgnoreCase("/emrpatients")) {
				Iterable<Patient> patients = patientRepository.findAllByDoctorId(patient.getDoctor().getId());
				model.addAttribute("patients", patients);
			    	return "emr/emr_patient_list";
			} else if (request.getServletPath().equalsIgnoreCase("/emrpatientprofile")) {
				return "emr/emr_patient_profile";
			} else {
				return "admin/patient_profile";
			}
		}
		
		List<Patient> patientMatch = patientRepository.findByFirstNameAndLastNameAndBirthdate(
			patient.getFirstName(), patient.getLastName(), patient.getBirthdate());
		
		if (patientMatch != null && patientMatch.size() > 0) {
		    redirect.addFlashAttribute("uxmessage", new UXMessage("WARNING", uxMessagePatientExists));
		} else {
		    redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", uxMessageText));
		}
		
		MultipartFile photoFile = patient.getPhotoFile();
		if(photoFile.getOriginalFilename().isEmpty()) {
			patient.setPatientPhoto(null);
		} else {
			try {
				String fileExt = photoFile.getOriginalFilename().substring(photoFile.getOriginalFilename().lastIndexOf("."));
				String fileName = "patient_photo_" + patient.getFirstName() + "_"+ patient.getLastName() + "_" + System.currentTimeMillis() + fileExt;
				FileDTO fileDTO = storageService.uploadFile(photoFile, fileName);
				patient.setPatientPhoto(fileDTO.getDownloadUri());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Patient dbPatient = patientRepository.save(patient);
		
		if (request.getServletPath().equalsIgnoreCase("/emrpatients")) {
			return "redirect:/emrpatients";
		} else if (request.getServletPath().equalsIgnoreCase("/emrpatientprofile")) {
			return "redirect:/emrpatientProfile/"+dbPatient.getId();
		} else {
			return "redirect:/view_patient/"+dbPatient.getId();
		}
	}
	
}
