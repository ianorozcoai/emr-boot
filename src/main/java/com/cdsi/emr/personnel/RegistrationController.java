package com.cdsi.emr.personnel;

import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.util.UXMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class RegistrationController {

    private PersonnelRepository personnelRepository;
    private PasswordEncoder passwordEncoder;

    public RegistrationController(
            PersonnelRepository personnelRepository
            ,PasswordEncoder passwordEncoder
            ) {
        this.personnelRepository = personnelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/registration")
    public String register(Model model) {
        model.addAttribute("personnel", new Personnel());
        return "personnel/register";
    }

    @PostMapping("/registration")
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
            return "personnel/login";
        }
        personnel.setPassword(this.passwordEncoder.encode(personnel.getPassword()));

        LocalDate today = LocalDate.now();
        personnel.setEndDate(today.plusMonths(1));
        this.personnelRepository.save(personnel);
        redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "System user added successfully."));
        return "redirect:/login";
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
