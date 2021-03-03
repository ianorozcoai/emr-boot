package com.cdsi.emr.icd;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.util.UXMessage;

@Controller
public class IcdController {
    private IcdRepository icdRepository;

    public IcdController (
            IcdRepository icdRepository
            ) {
        this.icdRepository = icdRepository;
    }

    @GetMapping("/icds")
    public String listAll(Model model) {
        List<Icd> icds = this.icdRepository.findAll();
        model.addAttribute("icds", icds);
        model.addAttribute(new Icd());
        return "admin/icd_list";
    }

    @GetMapping("/add_icd")
    public String addIcdForm(Model model) {
        model.addAttribute("icd", new Icd());
        return "admin/add_icd_form";
    }

    @GetMapping("edit_icd/{icdId}")
    public String editIcd(
            @PathVariable long icdId
            ,Model model
            ) {
        Optional<Icd> oIcd = this.icdRepository.findById(icdId);
        Icd icd = oIcd.orElseGet(com.cdsi.emr.icd.Icd::new);
        model.addAttribute("icd", icd);
        return "icd/add_icd_form";
    }

    @PostMapping("/icds")
    public String saveIcd(
            @Valid Icd icd
            ,Errors errors
            ,RedirectAttributes redirect
            ,Model model
            ) {
        if (errors.hasErrors()) {
            model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
            return "/icds";
        }
        this.icdRepository.save(icd);
        return "redirect:/icds";
    }

    @GetMapping("/icd/datalist")
    public ResponseEntity<FlexDatalistResult> flexDatalistForIcd() {
        List<Icd> result = this.icdRepository.findAll();
        return ResponseEntity.ok().body(new FlexDatalistResult(result));
    }
}
