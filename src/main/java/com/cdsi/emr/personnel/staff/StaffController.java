package com.cdsi.emr.personnel.staff;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.personnel.PersonnelDto;
import com.cdsi.emr.personnel.PersonnelRepository;
import com.cdsi.emr.util.UXMessage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller @AllArgsConstructor @Slf4j
public class StaffController {

    private PersonnelRepository staffRepository;
    private PasswordEncoder passwordEncoder;

    @GetMapping("/mystaffs")
    public String findAllStaffByDoctorId(Model model
            , @AuthenticationPrincipal Personnel personnel
            ) {
        List<Personnel> staffs = this.staffRepository.findAllByStaffSupervisorId(personnel.getId());
        model.addAttribute("personnels", staffs);
        model.addAttribute("personnel", new Personnel());
        model.addAttribute("personnelDto", new PersonnelDto());
        return "personnel/staff_list";
    }

    @PostMapping("/mystaffs")
    public String savePersonnel(
            @Valid Personnel personnel
            ,Errors errors
            ,final RedirectAttributes redirect
            ,Model model
            ,@AuthenticationPrincipal Personnel doctor
            ) {
        if (errors.hasErrors()) {
            model.addAttribute("isValidationErrorOnAdd", true);
            model.addAttribute("personnels", this.staffRepository.findAll());
            model.addAttribute("personnelDto", personnel);
            model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
            return "personnel/staff_list";
        }
        personnel.setPassword(this.passwordEncoder.encode(personnel.getPassword()));
        personnel.setStaffSupervisorId(doctor.getId());
        personnel.setUserType("ROLE_STAFF");
        this.staffRepository.save(personnel);
        redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Staff added successfully."));
        return "redirect:/mystaffs";
    }

    @PostMapping({"/mystaffs/edit"})
    public String editPersonnel(
            @Valid PersonnelDto personnelDto
            ,Errors errors
            ,final RedirectAttributes redirect
            ,Model model
            ) {
        if (errors.hasErrors()) {
            model.addAttribute("personnel", new Personnel());
            model.addAttribute("personnels", this.staffRepository.findAll());
            model.addAttribute("personnelDto", personnelDto);
            model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
            return "personnel/staff_list";
        }
        Personnel fromDb = this.staffRepository.findById(personnelDto.getId()).orElseGet(Personnel::new);
        Personnel fromDto = this.toPersonnel(personnelDto);
        fromDto.setUsername(fromDb.getUsername());
        fromDto.setPassword(fromDb.getPassword());
        fromDto.setStatus(fromDb.getStatus());
        fromDto.setStartDate(fromDb.getStartDate());
        fromDto.setEndDate(fromDb.getEndDate());
        fromDto.setStaffSupervisorId(fromDb.getStaffSupervisorId());
        fromDto.setUserType("ROLE_STAFF");

        this.staffRepository.save(fromDto);

        log.info("Successfully updated staff");
        redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Staff details updated successfully."));
        return "redirect:/mystaffs";
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
