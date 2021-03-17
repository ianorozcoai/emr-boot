package com.cdsi.emr.others;

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
public class EMRPatientOthersController {
    	private StorageService storageService;
	private EMRPatientOthersRepository emrPatientOthersRepository;
	private PatientRepository patientRepository;
	
	public EMRPatientOthersController (EMRPatientOthersRepository emrPatientOthersRepository, PatientRepository patientRepository
		,StorageService storageService) {
	    	this.storageService = storageService;
		this.emrPatientOthersRepository = emrPatientOthersRepository;
		this.patientRepository = patientRepository;
	}
	
	@GetMapping("/emrpatientOthers/{patientId}")
	public String listAll(Model model, @PathVariable long patientId) {
		List<EMRPatientOthers> emrPatientOthersList = emrPatientOthersRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		Patient patient = optionalPatient.get();
		model.addAttribute("patient", patient);
		model.addAttribute("emrPatientOthersList", emrPatientOthersList);
		model.addAttribute(new EMRPatientOthers());
		return "emr/emr_patient_others";
	}	
	
	@PostMapping("/emrpatientOthers")
	public String savePatientOthers(
			@Valid EMRPatientOthers emrPatientOthers
			,Errors errors
			,RedirectAttributes redirect
			,Model model
			) {
		if (errors.hasErrors()) {
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
			return "/emr_patient_others";
		}
		emrPatientOthersRepository.save(emrPatientOthers);
		return "redirect:/emrpatientOthers";
	}
	
	@GetMapping("/emrpatientOthers/{id}/json")
	public @ResponseBody EMRPatientOthers editEmrPatientLab(
			@PathVariable long id
			) {
		EMRPatientOthers others = emrPatientOthersRepository.findById(id)
				.orElseGet(() -> new EMRPatientOthers());
		return others;
	}
	
	@PostMapping("/upload/emrpatientOthers")
	@Transactional
	public ResponseEntity<FileInputResponse> saveEmrPatientOthers(
			@Valid EMRPatientOthers emrPatientOthers
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			) {
		FileInputResponse response = new FileInputResponse();
		List<String> initialPreview = new ArrayList<>();
		List<FileInputInitialPreviewConfig> initialPreviewConfig = new ArrayList<>();
		List<String> othersFileUrls = new ArrayList<>();
		long patientId = emrPatientOthers.getPatient().getId();
		MultipartFile[] files = emrPatientOthers.getOthersFiles();
		if(files.length > 0 && files[0].getOriginalFilename().isEmpty()) {
			EMRPatientOthers emrImg = emrPatientOthersRepository.findById(emrPatientOthers.getId())
					.orElseGet(() -> new EMRPatientOthers());
			othersFileUrls = emrImg.getOthersFileUrls();
		} else {
		    for (int i = 0; i < files.length; i++) {
		    	try {
		    		String fileExt = files[i].getOriginalFilename().substring(files[i].getOriginalFilename().lastIndexOf("."));
		    		String fileName = "patient_othersfile_" + patientId + '_' + i + "_" + System.currentTimeMillis() + fileExt;
		    		FileDTO fileDTO = storageService.uploadFile(files[i], fileName);
		    		othersFileUrls.add(fileDTO.getDownloadUri());
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
		emrPatientOthers.setOthersFileUrls(othersFileUrls);
		Patient patient = patientRepository.findById(patientId).orElseGet(() -> new Patient());
		emrPatientOthers.setPatient(patient);
		emrPatientOthersRepository.save(emrPatientOthers);
		
		response.setInitialPreview(initialPreview);
		response.setInitialPreviewConfig(initialPreviewConfig);
		return new ResponseEntity<>(response, null, HttpStatus.OK);
	}
}
