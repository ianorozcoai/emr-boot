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
public class EMRMedicalRequestController {

    private EMRMedicalRequestRepository emrMedicalRequestRepository;
    private EMRMedicalRequestGroupRepository emrMedicalRequestGroupRepository;


    public EMRMedicalRequestController (EMRMedicalRequestRepository emrMedicalRequestRepository, EMRMedicalRequestGroupRepository emrMedicalRequestGroupRepository) {
        this.emrMedicalRequestRepository = emrMedicalRequestRepository;
        this.emrMedicalRequestGroupRepository = emrMedicalRequestGroupRepository;
    }

    @GetMapping("/emrMedicalRequest")
    public String listAllMedicalRequest(Model model, @AuthenticationPrincipal Personnel doctor) {

        List<EMRMedicalRequest> emrMedicalRequestList = this.emrMedicalRequestRepository.findAllByDoctorIdOrderByMedicalRequestName(doctor.getId());
        List<EMRMedicalRequestGroup> emrMedicalRequestGroupList = this.emrMedicalRequestGroupRepository.findAllByDoctorIdOrderByMedicalRequestGroupName(doctor.getId());

        model.addAttribute("emrMedicalRequestGroupList", emrMedicalRequestGroupList);
        model.addAttribute("emrMedicalRequestList", emrMedicalRequestList);
        model.addAttribute("emrMedicalRequest", new EMRMedicalRequest());
        return "emr/emr_medical_request_list";
    }



    @PostMapping("/emrMedicalRequest")
    public String saveMedicalRequest(
            @Valid @ModelAttribute("emrMedicalRequest") EMRMedicalRequest emrMedicalRequest
            ,Errors errors
            ,final RedirectAttributes redirect
            ,Model model
            ,@AuthenticationPrincipal Personnel doctor
            ) {
        if (errors.hasErrors()) {
            List<EMRMedicalRequest> emrMedicalRequestList = this.emrMedicalRequestRepository.findAllByDoctorIdOrderByMedicalRequestName(doctor.getId());
            List<EMRMedicalRequestGroup> emrMedicalRequestGroupList = this.emrMedicalRequestGroupRepository.findAllByDoctorIdOrderByMedicalRequestGroupName(doctor.getId());

            model.addAttribute("emrMedicalRequestGroupList", emrMedicalRequestGroupList);
            model.addAttribute("emrMedicalRequestList", emrMedicalRequestList);
            model.addAttribute("emrMedicalRequest", emrMedicalRequest);
            model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
            return "emr/emr_medical_request_list";
        }
        emrMedicalRequest.setDoctorId(doctor.getId());
        this.emrMedicalRequestRepository.save(emrMedicalRequest);
        redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Medical Request successfully saved."));
        return "redirect:/emrMedicalRequest";
    }


}
