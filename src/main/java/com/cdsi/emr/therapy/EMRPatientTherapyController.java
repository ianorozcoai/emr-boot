package com.cdsi.emr.therapy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.cdsi.emr.procedures.EMRPatientProcedureRepository;
import com.cdsi.emr.procedures.EMRPatientProcedure;
import com.cdsi.emr.fileupload.FileDTO;
import com.cdsi.emr.fileupload.FileInputInitialPreviewConfig;
import com.cdsi.emr.fileupload.FileInputResponse;
import com.cdsi.emr.fileupload.StorageService;
import com.cdsi.emr.imaging.EMRPatientImaging;
import com.cdsi.emr.imaging.EMRPatientImagingRepository;
import com.cdsi.emr.labs.EMRPatientLaboratory;
import com.cdsi.emr.labs.EMRPatientLaboratoryRepository;
import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;
import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.util.UXMessage;

@Controller
public class EMRPatientTherapyController {
    private StorageService storageService;
    private EMRPatientTherapyRepository emrPatientTherapyRepository;
    private EMRPatientTherapyTypeRepository emrPatientTherapyTypeRepository;
    private EMRPatientProcedureRepository emrPatientProcedureRepository;
	private PatientRepository patientRepository;
	private EMRPatientLaboratoryRepository emrPatientLaboratoryRepository;
	private EMRPatientImagingRepository emrPatientImagingRepository;
	
	public EMRPatientTherapyController (EMRPatientProcedureRepository emrPatientProcedureRepository, PatientRepository patientRepository
		,StorageService storageService
		,EMRPatientLaboratoryRepository emrPatientLaboratoryRepository
		,EMRPatientImagingRepository emrPatientImagingRepository
		,EMRPatientTherapyRepository emrPatientTherapyRepository
		,EMRPatientTherapyTypeRepository emrPatientTherapyTypeRepository
			) {
	    	this.storageService = storageService;
	    	this.emrPatientProcedureRepository = emrPatientProcedureRepository;
	    	this.patientRepository = patientRepository;
	    	this.emrPatientLaboratoryRepository = emrPatientLaboratoryRepository;
			this.emrPatientImagingRepository = emrPatientImagingRepository;
			this.emrPatientTherapyRepository = emrPatientTherapyRepository;
			this.emrPatientTherapyTypeRepository = emrPatientTherapyTypeRepository;
	}
	
	@GetMapping("/emrpatientTherapy/{patientId}")
	public String listAll(Model model, @PathVariable long patientId, @AuthenticationPrincipal Personnel doctor) {
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
        
        List<EMRPatientTherapy> emrPatientTherapyList = emrPatientTherapyRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
        List<EMRPatientTherapyType> emrPatientTherapyTypeList = emrPatientTherapyTypeRepository.findAllByDoctorId(doctor.getId());
        
        model.addAttribute("patient", patient);
        model.addAttribute("emrPatientTherapyList", emrPatientTherapyList);
        model.addAttribute("allTherapyTypes", emrPatientTherapyTypeList);
        model.addAttribute(new EMRPatientTherapy());
        return "emr/emr_patient_therapy";
	}	
	
	@PostMapping("/emrpatientTherapy")
	public String savePatientTherapy(
			@Valid EMRPatientTherapy emrPatientTherapy
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			) {
	    
	        long patientId = emrPatientTherapy.getPatient().getId();
		if (errors.hasErrors()) {
            List<EMRPatientTherapy> emrPatientTherapyList = emrPatientTherapyRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
            List<EMRPatientTherapyType> emrPatientTherapyTypeList = emrPatientTherapyTypeRepository.findAll();
            Optional<Patient> optionalPatient = patientRepository.findById(patientId);
            Patient patient = optionalPatient.get();
            model.addAttribute("patient", patient);
            model.addAttribute("emrPatientTherapyList", emrPatientTherapyList);
            model.addAttribute("allTherapyTypes", emrPatientTherapyTypeList);
            model.addAttribute(new EMRPatientTherapy());
            model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
            return "emr/emr_patient_therapy";
        }
		emrPatientTherapyRepository.save(emrPatientTherapy);
		return "redirect:/emrpatientTherapy";
	}
	
	@GetMapping("/emrpatientTherapy/{id}/json")
	public @ResponseBody EMRPatientTherapy editEmrPatientTherapy(
			@PathVariable long id
			) {
		EMRPatientTherapy proc = emrPatientTherapyRepository.findById(id)
				.orElseGet(() -> new EMRPatientTherapy());
		return proc;
	}
	
	@PostMapping("/upload/emrpatientTherapy")
	@Transactional
	public ResponseEntity<FileInputResponse> saveEmrPatientTherapy(
			@Valid EMRPatientTherapy emrPatientTherapy
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			) {
		FileInputResponse response = new FileInputResponse();
		List<String> initialPreview = new ArrayList<>();
		List<FileInputInitialPreviewConfig> initialPreviewConfig = new ArrayList<>();
		List<String> therapyFileUrls = new ArrayList<>();
		long patientId = emrPatientTherapy.getPatient().getId();
		MultipartFile[] files = emrPatientTherapy.getTherapyFiles();
		if(files.length > 0 && files[0].getOriginalFilename().isEmpty()) {
			EMRPatientTherapy emrImg = emrPatientTherapyRepository.findById(emrPatientTherapy.getId())
					.orElseGet(() -> new EMRPatientTherapy());
			therapyFileUrls = emrImg.getTherapyFileUrls();
		} else {
		    for (int i = 0; i < files.length; i++) {
		    	try {
		    		String fileExt = files[i].getOriginalFilename().substring(files[i].getOriginalFilename().lastIndexOf("."));
		    		String fileName = "patient_therapyfile_" + patientId + '_' + i + "_" + System.currentTimeMillis() + fileExt;
		    		FileDTO fileDTO = storageService.uploadFile(files[i], fileName);
		    		therapyFileUrls.add(fileDTO.getDownloadUri());
		    		initialPreview.add(fileDTO.getDownloadUri());
		    		FileInputInitialPreviewConfig config = new FileInputInitialPreviewConfig();
		    		config.setKey(String.valueOf(i));
		    		config.setFileType(fileDTO.getContentType());
		    		config.setType("image");
		    		config.setCaption(fileName);
		    		config.setDownloadUrl(fileDTO.getDownloadUri());
		    		initialPreviewConfig.add(config);
		    	} catch (Exception e) {
		    		response.setError(e.getMessage());
		    		e.printStackTrace();
		    	}
		    }
		}
		emrPatientTherapy.setTherapyFileUrls(therapyFileUrls);
		Patient patient = patientRepository.findById(patientId).orElseGet(() -> new Patient());
		emrPatientTherapy.setPatient(patient);
		
		if(emrPatientTherapy.emrPatientTherapyType != null) {
			emrPatientTherapyRepository.save(emrPatientTherapy);
		} else {
			//redirect.addFlashAttribute("uxmessage", new UXMessage("ERRORDELETE", "Clinic cannot be deleted. It has been used in other records."));
			response.setError("Therapy Type is a mandatory field.");
		}
		
		
		
		response.setInitialPreview(initialPreview);
		response.setInitialPreviewConfig(initialPreviewConfig);
		return new ResponseEntity<>(response, null, HttpStatus.OK);
	}
}
