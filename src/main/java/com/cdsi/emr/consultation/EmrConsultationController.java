package com.cdsi.emr.consultation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;
import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.util.UXMessage;
import com.cdsi.emr.clinic.Clinic;
import com.cdsi.emr.clinic.ClinicRepository;
import com.cdsi.emr.hmo.HmoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class EmrConsultationController {

	private EmrConsultationRepository emrConsultationRepository;
	private PatientRepository patientRepository;
	private HmoRepository hmoRepository;
	private ObjectMapper mapper;
	private ClinicRepository clinicRepository;
	
	public EmrConsultationController(
			EmrConsultationRepository emrConsultationRepository
			,PatientRepository patientRepository
			,HmoRepository hmoRepository
			,ObjectMapper mapper
			,ClinicRepository clinicRepository
			) {
		this.emrConsultationRepository = emrConsultationRepository;
		this.patientRepository = patientRepository;
		this.hmoRepository = hmoRepository;
		this.mapper = mapper;
		this.clinicRepository = clinicRepository;
	}
	
	@GetMapping("/hmoCollection")
	public String listUnpaidHMOConsultation(Model model, Authentication auth) {
		Personnel loggedUser = (Personnel) auth.getPrincipal();
		
		List<EmrConsultation> hmoConsultations = emrConsultationRepository.findAllByPersonnelIdAndPaymentType(loggedUser.getId(), "HMO");
		List<EmrConsultation> unpaidHMOConsultations = new ArrayList<EmrConsultation>();
		List<EmrConsultation> paidHMOConsultations = new ArrayList<EmrConsultation>();
		
		for(EmrConsultation record : hmoConsultations) {
			if("Y".equals(record.getHmoPaid())) {
				paidHMOConsultations.add(record);
			} else {
				unpaidHMOConsultations.add(record);
			}
		}		

		model.addAttribute("unpaidHMOConsultations", unpaidHMOConsultations);
		model.addAttribute("paidHMOConsultations", paidHMOConsultations);
		model.addAttribute("markAsPaidDto", new MarkAsPaidRequestDto());

		return "emr/emr_hmocollection_list";
	}

	@PostMapping(value = "/hmoCollection", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String markAsPaidHmoConsultations(
			@Valid MarkAsPaidRequestDto markAsPaidDto
			, Errors errors
			, final RedirectAttributes redirect
	){
		if (errors.hasErrors()) {
			redirect.addFlashAttribute("uxmessage", new UXMessage("ERROR","Please mark at least 1."));
			return "redirect:/hmoCollection";
		}
		List<EmrConsultation> unpaidHMOConsultations =
				emrConsultationRepository.findAllByIdInAndPaymentType(markAsPaidDto.getConsultationIdList(), "HMO");
		unpaidHMOConsultations.stream().forEach(item -> {
			item.setHmoPaid("Y");
		});

		emrConsultationRepository.saveAll(unpaidHMOConsultations);

		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS","Selected are marked as paid."));
		return "redirect:/hmoCollection";
	}
	
	@GetMapping("/consultationStats")
	public String listConsultation(Model model, Authentication auth) {
		
		LocalDate dateFrom = LocalDate.now().minusDays(30);
		LocalDate dateTo = LocalDate.now();
		
		Personnel loggedUser = (Personnel) auth.getPrincipal();
		
		List<EmrConsultation> emrConsultations = emrConsultationRepository.findAllByPersonnelIdAndConsultationDateBetweenOrderByConsultationDateAsc(loggedUser.getId(), dateFrom, dateTo);
		
		EmrConsultationStatsDto dto = new EmrConsultationStatsDto();
		
		dto.setEmrConsultations(emrConsultations);
		
		int maleCount = 0;
		int femaleCount = 0;
		int hmoCount = 0;
		int cashCount = 0;
		BigDecimal cashAmount = BigDecimal.ZERO;
		
		for(EmrConsultation consultation : emrConsultations) {
			if("MALE".equals(consultation.getPatient().getGender())) {
				maleCount++;
			} else {
				femaleCount++;
			}
			
			if("HMO".equals(consultation.getPaymentType())) {
				hmoCount++;				
			} else {
				cashCount++;
				cashAmount = cashAmount.add(consultation.getConsultationFee() != null ? consultation.getConsultationFee() : BigDecimal.ZERO);
			}
		}
		
		dto.setDateFrom(dateFrom);
		dto.setDateTo(dateTo);
		dto.setTotalCashAmount(cashAmount);
		dto.setTotalCash(cashCount);
		dto.setTotalFemale(femaleCount);
		dto.setTotalHmo(hmoCount);
		dto.setTotalMale(maleCount);
		dto.setTotalPatients(femaleCount+maleCount);
		
		model.addAttribute("dto", dto);
				
		return "emr/emr_consultation_stats.html";
	}
	
	
	
	@PostMapping("/consultationStatistics")
	public String listConsultationStats(EmrConsultationStatsDto dto, Model model, Authentication auth) {
		
		Personnel loggedUser = (Personnel) auth.getPrincipal();
		
		List<EmrConsultation> emrConsultations = emrConsultationRepository
				.findAllByPersonnelIdAndConsultationDateBetweenOrderByConsultationDateAsc(loggedUser.getId(), dto.getDateFrom(), dto.getDateTo());
		
		Consumer<EmrConsultation> fetchDiagnosis = ec -> {
			List<EmrConsultationDiagnosis> diagnosis = ec.getDiagnosis();
			try {
				ec.setDiagnosisJSON(mapper.writeValueAsString(diagnosis));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		};
		
		emrConsultations.forEach(fetchDiagnosis);
				
		dto.setEmrConsultations(emrConsultations);
		
		int maleCount = 0;
		int femaleCount = 0;
		int hmoCount = 0;
		int cashCount = 0;
		BigDecimal cashAmount = BigDecimal.ZERO;
		
		for(EmrConsultation consultation : emrConsultations) {
			if("MALE".equals(consultation.getPatient().getGender())) {
				maleCount++;
			} else {
				femaleCount++;
			}
			
			if("HMO".equals(consultation.getPaymentType())) {
				hmoCount++;				
			} else {
				cashCount++;
				cashAmount = cashAmount.add(consultation.getConsultationFee() != null ? consultation.getConsultationFee() : BigDecimal.ZERO);
			}
		}
				
		dto.setTotalCashAmount(cashAmount);
		dto.setTotalCash(cashCount);
		dto.setTotalFemale(femaleCount);
		dto.setTotalHmo(hmoCount);
		dto.setTotalMale(maleCount);
		dto.setTotalPatients(femaleCount+maleCount);
		
		model.addAttribute("dto", dto);
		
		return "emr/emr_consultation_stats.html";
	}
	
	@PostMapping("/emrconsultations")
	public String saveEmrConsultation(
			@Valid EmrConsultation emrConsultation
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			,Authentication auth
			) {
		
		//Temporary Fix
		emrConsultation.setConsultationDate(emrConsultation.getConsultationDate().plusDays(1));
		
		Optional<Patient> optionalPatient = this.patientRepository.findById(emrConsultation.getPatient().getId());
		Patient patient = optionalPatient.get();
		
		Personnel loggedUser = (Personnel) auth.getPrincipal();		
		
		if (errors.hasErrors()) {
			model.addAttribute("uxmessage", new UXMessage("ERRORCONSULT","Please check items marked in red."));
			model.addAttribute("patient", patient);
			model.addAttribute("emrConsultations", this.emrConsultationRepository.findAll());
			model.addAttribute("hmos", hmoRepository.findAll());
			
			List<Clinic> clinicList = clinicRepository.findAllByDoctorId(loggedUser.getId());
			model.addAttribute("allClinics", clinicList);
			
			return "emr/emr_patient_record";
		}
		
		Optional<Clinic> optionalClinic = this.clinicRepository.findById(emrConsultation.getClinic().getId());
		Clinic clinic = optionalClinic.orElseGet(Clinic::new);
				
		String diagnosisJSON = emrConsultation.getDiagnosisJSON();
		if (diagnosisJSON == null || diagnosisJSON.isEmpty()) diagnosisJSON = "[]";
		try {
			List<EmrConsultationDiagnosis> diagnosis = mapper.readValue(diagnosisJSON, 
					new TypeReference<List<EmrConsultationDiagnosis>>() {});
			diagnosis.forEach(ecd -> ecd.setPatientId(emrConsultation.getPatient().getId()));
			emrConsultation.setDiagnosis(diagnosis);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			
			model.addAttribute("uxmessage", new UXMessage("ERRORCONSULT","Error converting JSON string."));
			model.addAttribute("patient", patient);
			model.addAttribute("emrConsultations", this.emrConsultationRepository.findAll());
			model.addAttribute("hmos", hmoRepository.findAll());
			
			List<Clinic> clinicList = clinicRepository.findAllByDoctorId(loggedUser.getId());
			model.addAttribute("allClinics", clinicList);
			
			return "emr/emr_patient_record";
		}
		emrConsultation.setClinic(clinic);
		emrConsultation.setPatient(patient); // set associated patient before saving
		emrConsultation.setPersonnel(loggedUser);
		this.emrConsultationRepository.save(emrConsultation);
		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESSCONSULT","Consultation data added successfully."));
		return "redirect:/emrpatientrecord/"+emrConsultation.getPatient().getId();
	}

}
