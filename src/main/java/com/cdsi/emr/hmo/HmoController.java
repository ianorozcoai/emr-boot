package com.cdsi.emr.hmo;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.util.UXMessage;

@Controller
public class HmoController {
	
	private HmoRepository hmoRepository;
	
	public HmoController (HmoRepository hmoRepository) {
		this.hmoRepository = hmoRepository;
	}
	
	@GetMapping("/hmos")
	public String listAll(Model model) {
		List<Hmo> hmos = hmoRepository.findAll();
		model.addAttribute("hmoList", hmos);
		model.addAttribute("hmo", new Hmo());
 
		return "admin/hmo_list";
	}
		
	@GetMapping("/add_hmo")
	public String addHmoForm(Model model) {
		model.addAttribute("hmo", new Hmo());
		return "admin/add_hmo_form";
	}
	
	@GetMapping("edit_hmo/{hmoId}")
	public String editHmo(
			@PathVariable long hmoId
			,Model model
			) {
		Optional<Hmo> oHmo = hmoRepository.findById(hmoId);
		Hmo hmo = oHmo.orElseGet(() -> new Hmo());
		model.addAttribute("hmo", hmo);
		return "admin/add_hmo_form";
	}
	
	@PostMapping("/hmos")
	public String saveHmo(
			@Valid Hmo hmo
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			) {
		if (errors.hasErrors()) {
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
			return "admin/hmo_list";
		}
		
		hmoRepository.save(hmo);
		
		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "HMO added successfully."));
		return "redirect:/hmos";
	}
}
