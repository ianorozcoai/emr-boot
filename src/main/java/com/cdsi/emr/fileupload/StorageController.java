package com.cdsi.emr.fileupload;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.personnel.PersonnelRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller @AllArgsConstructor
@RequestMapping("/file")
public class StorageController {

    private StorageService storageService;
    private PatientRepository patientRepository;
    private PersonnelRepository personnelRepository;

    @PostMapping({"/upload","/upload/profilePhoto"})
    @Transactional
    public ResponseEntity<FileDTO> uploadFile(
            @RequestParam("profilePhoto") MultipartFile file
            ,@RequestParam("patientId") long patientId
            ) throws Exception {
        
        // LOG 1: Check if request reaches here and print file details
        log.info("REST request to upload profilePhoto for patientId: {}", patientId);
        log.info("File Name: {}, Content-Type: {}, Size: {}", 
                 file.getOriginalFilename(), 
                 file.getContentType(), 
                 file.getSize());

        try {
            Patient patient = this.patientRepository.findById(patientId)
                    .orElseGet(Patient::new);

            // --- SAFE EXTENSION HANDLING START ---
            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg"; // Default if extension is missing

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            // LOG 2: Verify the extension logic worked
            log.info("Computed file extension: {}", extension);
            // --- SAFE EXTENSION HANDLING END ---

            // Construct the new filename safely
            String fileName = "patient_" + patient.getDoctor().getId() + "_" + patient.getId() + extension;
            log.info("Generated target filename: {}", fileName);

            FileDTO fileDTO = this.storageService.uploadFile(file, fileName);
            
            // LOG 3: Confirm storage service success
            log.info("StorageService upload successful. Download URI: {}", fileDTO.getDownloadUri());

            patient.setPatientPhoto(fileDTO.getDownloadUri());
            this.patientRepository.save(patient);
            
            return new ResponseEntity<>(fileDTO, null, HttpStatus.OK);

        } catch (Exception e) {
            // LOG 4: Catch any crash, print the FULL error, and re-throw so the frontend gets the error
            log.error("CRITICAL ERROR during file upload", e);
            throw e;
        }
    }

    @PostMapping("/upload/cliniclogo")
    @Transactional
    public ResponseEntity<FileDTO> updateClinicLogo(
            @RequestParam("clinicLogo") MultipartFile logo
            ,@AuthenticationPrincipal Personnel doctor
            ) throws Exception {
        
        log.info("REST request to upload clinicLogo for doctor: {}", doctor.getId());

        try {
            // --- SAFE EXTENSION HANDLING START ---
            String originalFilename = logo.getOriginalFilename();
            String extension = ".jpg"; 

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            // --- SAFE EXTENSION HANDLING END ---

            String filename = "clinic_logo_" + doctor.getId() + extension;
            
            FileDTO fileDTO = this.storageService.uploadFile(logo, filename);
            doctor.setClinicLogoUrl(fileDTO.getDownloadUri());
            this.personnelRepository.save(doctor);
            return new ResponseEntity<>(fileDTO, null, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("CRITICAL ERROR during clinic logo upload", e);
            throw e;
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Object> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws Exception {
        return this.storageService.downloadFile(fileName, request);
    }
}