package com.cdsi.emr.patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import com.cdsi.emr.clinic.Clinic;
import com.cdsi.emr.clinic.ClinicRepository;
import com.cdsi.emr.consultation.EmrConsultation;
import com.cdsi.emr.consultation.EmrConsultationDiagnosis;
import com.cdsi.emr.consultation.EmrConsultationRepository;
import com.cdsi.emr.fileupload.FileDTO;
import com.cdsi.emr.fileupload.StorageService;
import com.cdsi.emr.hmo.HmoRepository;
import com.cdsi.emr.imaging.EMRPatientImaging;
import com.cdsi.emr.imaging.EMRPatientImagingRepository;
import com.cdsi.emr.labs.EMRPatientLaboratory;
import com.cdsi.emr.labs.EMRPatientLaboratoryRepository;
import com.cdsi.emr.medication.EMRGenericsLookupRepository;
import com.cdsi.emr.medication.EMRPatientMedication;
import com.cdsi.emr.medication.EMRPatientMedicationForm;
import com.cdsi.emr.medication.EMRPatientMedicationItem;
import com.cdsi.emr.medication.EMRPatientMedicationRepository;
import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.procedures.EMRPatientProcedure;
import com.cdsi.emr.procedures.EMRPatientProcedureRepository;
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
	private ClinicRepository clinicRepository;
	private EMRPatientLaboratoryRepository emrPatientLaboratoryRepository;
	private EMRPatientImagingRepository emrPatientImagingRepository;
	private EMRPatientProcedureRepository emrPatientProcedureRepository;
	
	public PatientController (
		PatientRepository patientRepository
		,HmoRepository hmoRepository
		,EmrConsultationRepository emrConsultationRepository
		,EMRPatientMedicationRepository emrPatientMedicationRepository
		,EMRGenericsLookupRepository emrGenericsLookupRepository
		,PatientMedicalHistoryRepository patientMedicalHistoryRepository
		,ObjectMapper mapper
		,StorageService storageService
		,ClinicRepository clinicRepository
		,EMRPatientLaboratoryRepository emrPatientLaboratoryRepository
		,EMRPatientImagingRepository emrPatientImagingRepository
		,EMRPatientProcedureRepository emrPatientProcedureRepository
	) {
		this.patientRepository = patientRepository;
		this.hmoRepository = hmoRepository;
		this.emrConsultationRepository = emrConsultationRepository;
		this.emrPatientMedicationRepository = emrPatientMedicationRepository;
		this.emrGenericsLookupRepository = emrGenericsLookupRepository;
		this.patientMedicalHistoryRepository = patientMedicalHistoryRepository;
		this.mapper = mapper;
		this.storageService = storageService;
		this.clinicRepository = clinicRepository;
		this.emrPatientLaboratoryRepository = emrPatientLaboratoryRepository;
		this.emrPatientImagingRepository = emrPatientImagingRepository;
		this.emrPatientProcedureRepository = emrPatientProcedureRepository;
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
		Iterable<Patient> patients = patientRepository.findAllByDoctorIdOrderByFirstName(doctor.getId());
		Patient patient = new Patient();
		patient.setDoctor(doctor);
		patient.setIsActive("A");
		model.addAttribute("patient", patient);
		model.addAttribute("patients", patients);
		return "emr/emr_patient_list";
	}
	
	@GetMapping("/emrpatientrecord/{patientId}")
	public String viewPatientRecordByPatientId(Model model, @PathVariable long patientId, Authentication auth) {
		
		Personnel doctor = (Personnel) auth.getPrincipal();
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
        
        
        
		List<Clinic> clinicList = clinicRepository.findAllByDoctorIdOrderByName(doctor.getId());
		
		
		model.addAttribute("patient", patient);
		EmrConsultation emrConsultation = new EmrConsultation();
		emrConsultation.setPatient(patient);
		model.addAttribute("emrConsultation", emrConsultation);
		List<EmrConsultation> emrConsultations = this.emrConsultationRepository.findAllByPatientIdOrderByConsultationDateDesc(patientId);
		
		Consumer<EmrConsultation> fetchDiagnosis = ec -> {
			List<EmrConsultationDiagnosis> diagnosis = ec.getDiagnosis();
			try {
				ec.setDiagnosisJSON(mapper.writeValueAsString(diagnosis));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		};
		
		List<EmrConsultationDiagnosis> allDiagnosis = new ArrayList<EmrConsultationDiagnosis>();
		
		for(EmrConsultation consultation : emrConsultations) {
			allDiagnosis.addAll(consultation.getDiagnosis());
		}
		
		List<EMRPatientMedication> emrPatientMedicationList = emrPatientMedicationRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		model.addAttribute("emrPatientMedicationList", emrPatientMedicationList);

		emrConsultations.forEach(fetchDiagnosis);
		model.addAttribute("emrConsultations", emrConsultations);
		model.addAttribute("allDiagnosis", allDiagnosis);
		model.addAttribute("hmos", hmoRepository.findAllByOrderByHmoName());
		model.addAttribute("allClinics", clinicList);
		model.addAttribute("selectedConsultationId", 0);
		model.addAttribute("mode", "N");
		
		return "emr/emr_patient_record";
	}
	
	@GetMapping("/emrpatientrecord/{patientId}/{consultationId}")
    public String viewPatientRecordFromDashboard(Model model
            , @PathVariable long patientId
            , @PathVariable long consultationId, Authentication auth) {
		
		Personnel doctor = (Personnel) auth.getPrincipal();
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
        EmrConsultation emrConsultation = new EmrConsultation();
        emrConsultation.setPatient(patient);
        
        
        
        List<EmrConsultation> emrConsultations = this.emrConsultationRepository.findAllByPatientIdOrderByConsultationDateDesc(patientId);
        Consumer<EmrConsultation> fetchDiagnosis = ec -> {
            List<EmrConsultationDiagnosis> diagnosis = ec.getDiagnosis();
            try {
                ec.setDiagnosisJSON(mapper.writeValueAsString(diagnosis));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        };
        
        List<EmrConsultationDiagnosis> allDiagnosis = new ArrayList<EmrConsultationDiagnosis>();
		
		for(EmrConsultation consultation : emrConsultations) {
			allDiagnosis.addAll(consultation.getDiagnosis());			
		}
		
		String mode = "E";
		
		for(EmrConsultation consultation : emrConsultations) {			
			if(consultationId == 0 && !consultation.getDiagnosis().isEmpty()) {
				consultationId = consultation.getId();
				mode = "L";
				break;
	        }
		}
		
		model.addAttribute("allDiagnosis", allDiagnosis);
        
        List<EMRPatientMedication> emrPatientMedicationList = emrPatientMedicationRepository.findByPatientId(patientId);
        List<String> emrGenericsLookupList = emrGenericsLookupRepository.findDistinctByGenericName();
        
        emrConsultations.forEach(fetchDiagnosis);
        model.addAttribute("emrConsultations", emrConsultations);
        model.addAttribute("hmos", hmoRepository.findAllByOrderByHmoName());
        
        EMRPatientMedicationForm emrPatientMedicationForm = new EMRPatientMedicationForm();
        emrPatientMedicationForm.getEmrPatientMedicationItems().add(new EMRPatientMedicationItem());
        model.addAttribute("emrPatientMedicationList", emrPatientMedicationList);
        model.addAttribute("emrGenericsLookupList", emrGenericsLookupList);
        model.addAttribute("emrPatientMedicationForm", emrPatientMedicationForm);
        model.addAttribute("dosages", EHRConstants.DOSAGE);
        model.addAttribute("allClinics", clinicRepository.findAllByDoctorId(doctor.getId()));
        model.addAttribute("selectedConsultationId", consultationId);        
        model.addAttribute("emrConsultation", emrConsultation);        
        model.addAttribute("mode", mode);
        
        return "emr/emr_patient_record";
    }
	
	@GetMapping("/emrpatientProfile/{patientId}")
	public String viewPatientProfileByPatientId(Model model, @PathVariable long patientId) {
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
		
//		List<PatientMedicalHistory> patientMedicalHistoryList = patientMedicalHistoryRepository.findByPatientId(patientId);
//		model.addAttribute("patientMedicalHistory", !patientMedicalHistoryList.isEmpty() ? patientMedicalHistoryList.get(0) : new PatientMedicalHistory());
		model.addAttribute("patient", patient);
		EmrConsultation emrConsultation = new EmrConsultation();
		emrConsultation.setPatient(patient);
		return "emr/emr_patient_profile";
	}
	
	@GetMapping("/emrpatientHistory/{patientId}")
	public String viewPatientHistoryByPatientId(Model model, @PathVariable long patientId) {
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
		
		List<PatientMedicalHistory> patientMedicalHistoryList = patientMedicalHistoryRepository.findByPatientId(patientId);
		model.addAttribute("patientMedicalHistory", !patientMedicalHistoryList.isEmpty() ? patientMedicalHistoryList.get(0) : new PatientMedicalHistory());
		model.addAttribute("patient", patient);
		EmrConsultation emrConsultation = new EmrConsultation();
		emrConsultation.setPatient(patient);
		return "emr/emr_patient_history";
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
				return "emr/emr_patient_history";
			} else {
				return "admin/patient_profile";
			}
			
		}
		
		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Patient Medical History has been successfully saved."));
		
		patientMedicalHistoryRepository.save(patientMedicalHistory);
		
		if (request.getServletPath().equalsIgnoreCase("/emrPatientMedicalHistory")) {
			return "redirect:/emrpatientHistory/"+patientMedicalHistory.getPatient().getId();
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
			model.addAttribute("hmos", hmoRepository.findAllByOrderByHmoName());
			if (request.getServletPath().equalsIgnoreCase("/emrpatients")) {
				Iterable<Patient> patients = patientRepository.findAllByDoctorIdOrderByFirstName(patient.getDoctor().getId());
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
		
		if (!request.getServletPath().equalsIgnoreCase("/emrpatientprofile")) {
			MultipartFile photoFile = patient.getPhotoFile();
			if(photoFile == null || photoFile.getOriginalFilename().isEmpty()) {
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
