package com.cdsi.emr.fileupload;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/file")
public class StorageController {

    private StorageService storageService;
    private PatientRepository patientRepository;

    public StorageController(
    		StorageService storageService
    		,PatientRepository patientRepository
    		) {
        this.storageService = storageService;
        this.patientRepository = patientRepository;
    }


    @PostMapping({"/upload","/upload/profilePhoto"})
    @Transactional
    public ResponseEntity<FileDTO> uploadFile(
    		@RequestParam("profilePhoto") MultipartFile file
    		,@RequestParam("patientId") long patientId
    		) throws Exception {
        log.info("REST request to upload file");
        //upload files
        Patient patient = this.patientRepository.findById(patientId)
        		.orElseGet(() -> new Patient());
        String fileName = "patient_" + patient.getId() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")); 
        FileDTO fileDTO = storageService.uploadFile(file, fileName);
        patient.setPatientPhoto(fileDTO.getDownloadUri());
        patientRepository.save(patient);
        return new ResponseEntity<>(fileDTO, null, HttpStatus.OK);
    }


    /*private FileInfo toFileInfo(FileDTO fileDTO, String username, long productId) {
		return new FileInfo(0L,
				fileDTO.getFilename(),
				fileDTO.getContentType(),
				fileDTO.getDownloadUri(),
				fileDTO.getFileSize(),
				LocalDate.now(),
				username,
				productId
				);
	}*/


	@GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Object> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws Exception {
        return storageService.downloadFile(fileName, request);
    }
}