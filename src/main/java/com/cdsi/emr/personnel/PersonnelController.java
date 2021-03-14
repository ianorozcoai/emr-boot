package com.cdsi.emr.personnel;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class PersonnelController {

    private PersonnelRepository personnelRepository;
    private PasswordEncoder passwordEncoder;

    public PersonnelController(
            PersonnelRepository personnelRepository
            ,PasswordEncoder passwordEncoder
            ) {
        this.personnelRepository = personnelRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

        log.info("Saving updated Personnel in Database and in Security Context");
        Authentication authentication = new UsernamePasswordAuthenticationToken(this.personnelRepository.save(fromDto),
                fromDb.getPassword(),
                SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
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
