package com.cdsi.emr.medicalrequest;


import java.util.List;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.util.UXMessage;

@Controller
public class EMRMedicalRequestGroupController {

    private EMRMedicalRequestGroupRepository emrMedicalRequestGroupRepository;


    public EMRMedicalRequestGroupController (EMRMedicalRequestGroupRepository emrMedicalRequestGroupRepository) {
        this.emrMedicalRequestGroupRepository = emrMedicalRequestGroupRepository;
    }

    @GetMapping("/emrMedicalRequestGroup")
    public String listAllMedicalRequestGroup(Model model, @AuthenticationPrincipal Personnel doctor) {

        List<EMRMedicalRequestGroup> emrMedicalRequestGroupList = this.emrMedicalRequestGroupRepository.findAllByDoctorIdOrderByMedicalRequestGroupName(doctor.getId());

        model.addAttribute("emrMedicalRequestGroupList", emrMedicalRequestGroupList);
        model.addAttribute("emrMedicalRequestGroup", new EMRMedicalRequestGroup());
        return "emr/emr_medical_request_group_list";
    }



    @PostMapping("/emrMedicalRequestGroup")
    public String saveMedicalRequestGroup(
            @Valid @ModelAttribute("emrMedicalRequestGroup") EMRMedicalRequestGroup emrMedicalRequestGroup
            ,Errors errors
            ,final RedirectAttributes redirect
            ,Model model
            ,@AuthenticationPrincipal Personnel doctor
            ) {
        if (errors.hasErrors()) {
            List<EMRMedicalRequestGroup> emrMedicalRequestGroupList = this.emrMedicalRequestGroupRepository.findAllByDoctorIdOrderByMedicalRequestGroupName(doctor.getId());

            model.addAttribute("emrMedicalRequestGroupList", emrMedicalRequestGroupList);
            model.addAttribute("emrMedicalRequestGroup", emrMedicalRequestGroup);
            model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
            return "emr/emr_medical_request_group_list";
        }
        emrMedicalRequestGroup.setDoctorId(doctor.getId());
        this.emrMedicalRequestGroupRepository.save(emrMedicalRequestGroup);
        redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Medical Request Group successfully saved."));
        return "redirect:/emrMedicalRequestGroup";
    }


}
