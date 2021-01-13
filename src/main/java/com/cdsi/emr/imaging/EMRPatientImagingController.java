package com.cdsi.emr.imaging;

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
public class EMRPatientImagingController {
	private StorageService storageService;
	private EMRPatientImagingRepository emrPatientImagingRepository;
	private EMRPatientImagingTypeRepository emrPatientImagingTypeRepository;
	private PatientRepository patientRepository;
	
	public EMRPatientImagingController (EMRPatientImagingRepository emrPatientImagingRepository
			,EMRPatientImagingTypeRepository emrPatientImagingTypeRepository
			,PatientRepository patientRepository
			,StorageService storageService
	        ) {
		this.emrPatientImagingRepository = emrPatientImagingRepository;
		this.emrPatientImagingTypeRepository = emrPatientImagingTypeRepository;
		this.patientRepository = patientRepository;
		this.storageService = storageService;
	}
	
	@GetMapping("/emrpatientImaging/{patientId}")
	public String listAll(Model model, @PathVariable long patientId) {
		List<EMRPatientImaging> emrPatientImagingList = emrPatientImagingRepository.findByPatientIdOrderByDateCreatedAsc(patientId);
		List<EMRPatientImagingType> emrPatientImagingTypeList = emrPatientImagingTypeRepository.findAll();
		Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		Patient patient = optionalPatient.get();
		model.addAttribute("patient", patient);
		model.addAttribute("emrPatientImagingList", emrPatientImagingList);
		model.addAttribute("allImagingTypes", emrPatientImagingTypeList);
		model.addAttribute(new EMRPatientImaging());
		return "emr/emr_patient_imaging";
	}	
	
	@PostMapping("/emrpatientImaging")
	public String savePatientImaging(
			@Valid EMRPatientImaging emrPatientImaging
			,Errors errors
			,RedirectAttributes redirect
			,Model model
			) {
		if (errors.hasErrors()) {
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
			return "/emr_patient_imaging";
		}
		emrPatientImagingRepository.save(emrPatientImaging);
		return "redirect:/emrpatientImaging";
	}
	
	@PostMapping("/upload/emrpatientImaging")
	@Transactional
	public ResponseEntity<FileInputResponse> saveEmrPatientLaboratory(
			@Valid EMRPatientImaging emrPatientImaging
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			) {
		FileInputResponse response = new FileInputResponse();
		List<String> initialPreview = new ArrayList<>();
		List<FileInputInitialPreviewConfig> initialPreviewConfig = new ArrayList<>();
		List<String> imgFileUrls = new ArrayList<>();
		long patientId = emrPatientImaging.getPatient().getId();
		MultipartFile[] files = emrPatientImaging.getImgFiles();
		if(files.length > 0 && files[0].getOriginalFilename().isEmpty()) {
			EMRPatientImaging emrImg = emrPatientImagingRepository.findById(emrPatientImaging.getId())
					.orElseGet(() -> new EMRPatientImaging());
			imgFileUrls = emrImg.getImgFileUrls();
		} else {
		    for (int i = 0; i < files.length; i++) {
		    	try {
		    		String fileExt = files[i].getOriginalFilename().substring(files[i].getOriginalFilename().lastIndexOf("."));
		    		String fileName = "patient_imgfile_" + patientId + '_' + i + "_" + System.currentTimeMillis() + fileExt;
		    		FileDTO fileDTO = storageService.uploadFile(files[i], fileName);
		    		imgFileUrls.add(fileDTO.getDownloadUri());
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
		emrPatientImaging.setImgFileUrls(imgFileUrls);
		Patient patient = patientRepository.findById(patientId).orElseGet(() -> new Patient());
		emrPatientImaging.setPatient(patient);
		emrPatientImagingRepository.save(emrPatientImaging);
		
		response.setInitialPreview(initialPreview);
		response.setInitialPreviewConfig(initialPreviewConfig);
		return new ResponseEntity<>(response, null, HttpStatus.OK);
	}
}
