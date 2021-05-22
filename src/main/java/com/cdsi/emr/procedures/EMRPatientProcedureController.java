package com.cdsi.emr.procedures;

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

import com.cdsi.emr.procedures.EMRPatientProcedureTypeRepository;
import com.cdsi.emr.procedures.EMRPatientProcedure;
import com.cdsi.emr.procedures.EMRPatientProcedureType;
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
public class EMRPatientProcedureController {
    private StorageService storageService;
    private EMRPatientProcedureRepository emrPatientProcedureRepository;
    private EMRPatientProcedureTypeRepository emrPatientProcedureTypeRepository;
	private PatientRepository patientRepository;
	private EMRPatientLaboratoryRepository emrPatientLaboratoryRepository;
	private EMRPatientImagingRepository emrPatientImagingRepository;
	
	public EMRPatientProcedureController (EMRPatientProcedureRepository emrPatientProcedureRepository, PatientRepository patientRepository
		, EMRPatientProcedureTypeRepository emrPatientProcedureTypeRepository, StorageService storageService
		,EMRPatientLaboratoryRepository emrPatientLaboratoryRepository
		,EMRPatientImagingRepository emrPatientImagingRepository
			) {
	    	this.storageService = storageService;
	    	this.emrPatientProcedureRepository = emrPatientProcedureRepository;
	    	this.emrPatientProcedureTypeRepository = emrPatientProcedureTypeRepository;
	    	this.patientRepository = patientRepository;
	    	this.emrPatientLaboratoryRepository = emrPatientLaboratoryRepository;
			this.emrPatientImagingRepository = emrPatientImagingRepository;
	}
	
	@GetMapping("/emrpatientProcedure/{patientId}")
	public String listAll(Model model, @PathVariable long patientId, @AuthenticationPrincipal Personnel doctor) {
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
        
        List<EMRPatientProcedureType> emrPatientProcedureTypeList = emrPatientProcedureTypeRepository.findAllByDoctorId(doctor.getId());
        
        model.addAttribute("patient", patient);
        model.addAttribute("emrPatientProcedureList", emrPatientProcedureList);
        model.addAttribute("allProcedureTypes", emrPatientProcedureTypeList);
        model.addAttribute(new EMRPatientProcedure());
        return "emr/emr_patient_procedure";
	}	
	
	@PostMapping("/emrpatientProcedure")
	public String savePatientProcedure(
			@Valid EMRPatientProcedure emrPatientProcedure
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			) {
	    
	        long patientId = emrPatientProcedure.getPatient().getId();
		if (errors.hasErrors()) {
            List<EMRPatientProcedure> emrPatientProcedureList = emrPatientProcedureRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
            List<EMRPatientProcedureType> emrPatientProcedureTypeList = emrPatientProcedureTypeRepository.findAll();
            Optional<Patient> optionalPatient = patientRepository.findById(patientId);
            Patient patient = optionalPatient.get();
            model.addAttribute("patient", patient);
            model.addAttribute("emrPatientProcedureList", emrPatientProcedureList);
            model.addAttribute("allProcedureTypes", emrPatientProcedureTypeList);
            model.addAttribute(new EMRPatientProcedure());
            model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
            return "emr/emr_patient_procedure";
        }
		emrPatientProcedureRepository.save(emrPatientProcedure);
		return "redirect:/emrpatientProcedure";
	}
	
	@GetMapping("/emrpatientProcedure/{id}/json")
	public @ResponseBody EMRPatientProcedure editEmrPatientProcedure(
			@PathVariable long id
			) {
		EMRPatientProcedure proc = emrPatientProcedureRepository.findById(id)
				.orElseGet(() -> new EMRPatientProcedure());
		return proc;
	}
	
	@PostMapping("/upload/emrpatientProcedure")
	@Transactional
	public ResponseEntity<FileInputResponse> saveEmrPatientProcedure(
			@Valid EMRPatientProcedure emrPatientProcedure
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			) {
		FileInputResponse response = new FileInputResponse();
		List<String> initialPreview = new ArrayList<>();
		List<FileInputInitialPreviewConfig> initialPreviewConfig = new ArrayList<>();
		List<String> procedureFileUrls = new ArrayList<>();
		long patientId = emrPatientProcedure.getPatient().getId();
		MultipartFile[] files = emrPatientProcedure.getProcedureFiles();
		if(files.length > 0 && files[0].getOriginalFilename().isEmpty()) {
			EMRPatientProcedure emrImg = emrPatientProcedureRepository.findById(emrPatientProcedure.getId())
					.orElseGet(() -> new EMRPatientProcedure());
			procedureFileUrls = emrImg.getProcedureFileUrls();
		} else {
		    for (int i = 0; i < files.length; i++) {
		    	try {
		    		String fileExt = files[i].getOriginalFilename().substring(files[i].getOriginalFilename().lastIndexOf("."));
		    		String fileName = "patient_procedurefile_" + patientId + '_' + i + "_" + System.currentTimeMillis() + fileExt;
		    		FileDTO fileDTO = storageService.uploadFile(files[i], fileName);
		    		procedureFileUrls.add(fileDTO.getDownloadUri());
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
		emrPatientProcedure.setProcedureFileUrls(procedureFileUrls);
		Patient patient = patientRepository.findById(patientId).orElseGet(() -> new Patient());
		emrPatientProcedure.setPatient(patient);
		emrPatientProcedureRepository.save(emrPatientProcedure);
		
		response.setInitialPreview(initialPreview);
		response.setInitialPreviewConfig(initialPreviewConfig);
		return new ResponseEntity<>(response, null, HttpStatus.OK);
	}
}
