package com.cdsi.emr.procedures;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.fileupload.FileDTO;
import com.cdsi.emr.fileupload.FileInputInitialPreviewConfig;
import com.cdsi.emr.fileupload.FileInputResponse;
import com.cdsi.emr.fileupload.StorageService;
import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;
import com.cdsi.emr.util.UXMessage;

@Controller
public class EMRPatientProcedureController {
    	private StorageService storageService;
    	private EMRPatientProcedureRepository emrPatientProcedureRepository;
	private PatientRepository patientRepository;
	
	public EMRPatientProcedureController (EMRPatientProcedureRepository emrPatientProcedureRepository, PatientRepository patientRepository
		,StorageService storageService
			) {
	    	this.storageService = storageService;
	    	this.emrPatientProcedureRepository = emrPatientProcedureRepository;
		this.patientRepository = patientRepository;
	}
	
	@GetMapping("/emrpatientProcedure/{patientId}")
	public String listAll(Model model, @PathVariable long patientId) {
		List<EMRPatientProcedure> emrPatientProcedureList = emrPatientProcedureRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		Patient patient = optionalPatient.get();
		model.addAttribute("patient", patient);
		model.addAttribute("emrPatientProcedureList", emrPatientProcedureList);
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
		if (errors.hasErrors()) {
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
