package com.cdsi.emr.personnel;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.fileupload.FileDTO;
import com.cdsi.emr.fileupload.StorageService;
import com.cdsi.emr.util.UXMessage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller @AllArgsConstructor
public class PersonnelController {

    private PersonnelRepository personnelRepository;
    private PasswordEncoder passwordEncoder;
    private StorageService storageService;

    @GetMapping("/personnels")
    public String listAll(Model model) {
        List<Personnel> personnels = this.personnelRepository.findAll();
        model.addAttribute("personnels", personnels);
        model.addAttribute("personnel", new Personnel());
        model.addAttribute("personnelDto", new PersonnelDto());
        return "personnel/personnel_list";
    }

    @GetMapping("/emrDoctorProfile")
    public String getDoctorProfile(Model model, Authentication auth) {
        Personnel doctor = (Personnel) auth.getPrincipal();

        //Optional<Personnel> oPersonnelProfile = personnelRepository.findById(doctor.getId());
        //Personnel personnelProfile = oPersonnelProfile.orElseGet(() -> new Personnel());

        model.addAttribute("personnelDto", doctor);
        return "emr/emr_doctor_profile";
    }

    @GetMapping("/emrDoctorProfilePhoto")
    public String getDoctorProfilePhoto(Model model) {
        return "emr/emr_doctor_profile_photo";
    }

    @PostMapping("/emr/profilephoto")
    @Transactional
    public String saveDoctorProfilePhoto(@RequestParam MultipartFile photoFile
            ,@AuthenticationPrincipal Personnel doctor
            ,final RedirectAttributes redirect
            ) {
        if(photoFile == null || photoFile.getOriginalFilename().isEmpty()) {
            if(doctor.getProfilePhotoUrl() == null || doctor.getProfilePhotoUrl().isEmpty()) {
                doctor.setProfilePhotoUrl(null);
            }
            redirect.addFlashAttribute("uxmessage", new UXMessage("ERROR", "Profile Photo is blank."));
        } else {
            try {
                String fileExt = photoFile.getOriginalFilename().substring(photoFile.getOriginalFilename().lastIndexOf("."));
                String fileName = "doctor_profile_photo_" + doctor.getFirstName() + "_"+ doctor.getLastName() + "_" + System.currentTimeMillis() + fileExt;
                FileDTO fileDTO = this.storageService.uploadFile(photoFile, fileName);
                doctor.setProfilePhotoUrl(fileDTO.getDownloadUri());
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.personnelRepository.updateProfilePhotoUrl(doctor.getId(), doctor.getProfilePhotoUrl());
            redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Profile Photo updated successfully."));
        }
        return "redirect:/emrDoctorProfilePhoto";
    }

    @GetMapping("/cliniclogo")
    public String updateClinicLogo() {
        return "admin/cliniclogo";
    }

    @PostMapping("/personnels")
    public String savePersonnel(
            @Valid Personnel personnel
            ,Errors errors
            ,final RedirectAttributes redirect
            ,Model model
            ) {
        if (errors.hasErrors()) {
            model.addAttribute("isValidationErrorOnAdd", true);
            model.addAttribute("personnels", this.personnelRepository.findAll());
            model.addAttribute("personnelDto", personnel);
            model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
            return "personnel/personnel_list";
        }
        personnel.setPassword(this.passwordEncoder.encode(personnel.getPassword()));
        this.personnelRepository.save(personnel);
        redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "System user added successfully."));
        return "redirect:/personnels";
    }

    @PostMapping({"/personnels/edit", "/emr/profile/edit","/myProfile"})
    public String editPersonnel(
            @Valid PersonnelDto personnelDto
            ,Errors errors
            ,final RedirectAttributes redirect
            ,Model model
            ,HttpServletRequest request
            ,@AuthenticationPrincipal Personnel doctor
            ) {
        if (errors.hasErrors()) {
            model.addAttribute("personnel", new Personnel());
            model.addAttribute("personnels", this.personnelRepository.findAll());
            model.addAttribute("personnelDto", personnelDto);
            model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
            if (request.getServletPath().equalsIgnoreCase("/emr/profile/edit")) {
                return "emr/emr_doctor_profile";
            } else {
                return "personnel/personnel_list";
            }
        }
        Personnel fromDb = this.personnelRepository.findById(personnelDto.getId()).orElseGet(Personnel::new);
        Personnel fromDto = this.toPersonnel(personnelDto);
        fromDto.setUsername(fromDb.getUsername());
        fromDto.setPassword(fromDb.getPassword());
        fromDto.setStatus(fromDb.getStatus());
        fromDto.setStartDate(fromDb.getStartDate());
        fromDto.setEndDate(fromDb.getEndDate());
        fromDto.setProfilePhotoUrl(fromDb.getProfilePhotoUrl());

        this.personnelRepository.save(fromDto);

        // TODO: Update AuthenticationPricipal if DTO is the same as logged in user (/myProfile & /emr/profile/edit)
        this.updateLoggedInDoctor(doctor, fromDto);

        log.info("Successfully saved changes of Personnel {} - {}, {}",
                fromDb.getUsername(),
                fromDb.getLastName(),
                fromDb.getFirstName());

        if (request.getServletPath().equalsIgnoreCase("/emr/profile/edit")) {
            redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Profile updated successfully."));
            return "redirect:/emrDoctorProfile";
        } else if (request.getServletPath().equalsIgnoreCase("/myProfile")) {
            redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Profile updated successfully."));
            return "redirect:/myProfile";
        } else {
            redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "System user updated successfully."));
            return "redirect:/personnels";
        }
    }

    private void updateLoggedInDoctor(Personnel doctor, Personnel fromDto) {
        // TODO compare fields, if doctor is different from Dto, update personnel
    	if (!doctor.getFirstName().equalsIgnoreCase(fromDto.getFirstName())) {
            doctor.setFirstName(fromDto.getFirstName());
        }
        if (!doctor.getLastName().equalsIgnoreCase(fromDto.getLastName())) {
            doctor.setLastName(fromDto.getLastName());
        }
        if (!doctor.getLastName().equalsIgnoreCase(fromDto.getLastName())) {
            doctor.setLastName(fromDto.getLastName());
        }
        if (!doctor.getGender().equalsIgnoreCase(fromDto.getGender())) {
            doctor.setGender(fromDto.getGender());
        }
        if (!doctor.getContactNumber().equalsIgnoreCase(fromDto.getContactNumber())) {
            doctor.setContactNumber(fromDto.getContactNumber());
        }
        if (!doctor.getEmail().equalsIgnoreCase(fromDto.getEmail())) {
            doctor.setEmail(fromDto.getEmail());
        }
        if (!doctor.getAddress().equalsIgnoreCase(fromDto.getAddress())) {
            doctor.setAddress(fromDto.getAddress());
        }
        if (!doctor.getCredentials().equalsIgnoreCase(fromDto.getCredentials())) {
            doctor.setCredentials(fromDto.getCredentials());
        }
        if (!doctor.getSpecialization().equalsIgnoreCase(fromDto.getSpecialization())) {
            doctor.setSpecialization(fromDto.getSpecialization());
        }
        if (!doctor.getLicenseNumber().equalsIgnoreCase(fromDto.getLicenseNumber())) {
            doctor.setLicenseNumber(fromDto.getLicenseNumber());
        }
        if (!doctor.getPtrNumber().equalsIgnoreCase(fromDto.getPtrNumber())) {
            doctor.setPtrNumber(fromDto.getPtrNumber());
        }
        if (!doctor.getSNumber().equalsIgnoreCase(fromDto.getSNumber())) {
            doctor.setSNumber(fromDto.getSNumber());
        }
    }

    private Personnel toPersonnel(PersonnelDto p) {
        return new Personnel(
                p.getId(),
                null, //username
                null, //password
                p.getFirstName(),
                p.getLastName(),
                p.getGender(),
                p.getAddress(),
                p.getContactNumber(),
                p.getEmail(),
                p.getStatus(),
                p.getUserType(),
                p.getStaffCount(),
                p.getSuperiorId(),
                p.getProfilePhotoUrl(),
                p.getCredentials(),
                p.getLicenseNumber(),
                p.getSpecialization(),
                p.getPtrNumber(),
                p.getSNumber(),
                null, //startDate
                null, //endDate
                p.getClinicLogoUrl()
                );
    }
}
