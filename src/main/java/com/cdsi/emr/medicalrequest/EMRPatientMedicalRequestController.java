package com.cdsi.emr.medicalrequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.cdsi.emr.imaging.EMRPatientImaging;
import com.cdsi.emr.imaging.EMRPatientImagingRepository;
import com.cdsi.emr.labs.EMRPatientLaboratory;
import com.cdsi.emr.labs.EMRPatientLaboratoryRepository;
import com.cdsi.emr.medication.EMRPatientMedicationItem;
import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;
import com.cdsi.emr.procedures.EMRPatientProcedure;
import com.cdsi.emr.procedures.EMRPatientProcedureRepository;
import com.cdsi.emr.util.UXMessage;

@Controller
public class EMRPatientMedicalRequestController {
    private EMRPatientMedicalRequestRepository emrPatientMedicalRequestRepository;
    private EMRPatientMedicalRequestItemRepository emrPatientMedicalRequestItemRepository;
	private PatientRepository patientRepository;	
	private EMRPatientLaboratoryRepository emrPatientLaboratoryRepository;
	private EMRPatientImagingRepository emrPatientImagingRepository;
	private EMRPatientProcedureRepository emrPatientProcedureRepository;
	private EMRMedicalRequestRepository emrMedicalRequestRepository;
	
	
	public EMRPatientMedicalRequestController (EMRPatientMedicalRequestRepository emrPatientMedicalRequestRepository,
		PatientRepository patientRepository,
		EMRPatientMedicalRequestItemRepository emrPatientMedicalRequestItemRepository,
	    EMRPatientLaboratoryRepository emrPatientLaboratoryRepository,
		EMRPatientImagingRepository emrPatientImagingRepository,
		EMRPatientProcedureRepository emrPatientProcedureRepository,
		EMRMedicalRequestRepository emrMedicalRequestRepository
			) {
	    this.emrPatientMedicalRequestRepository = emrPatientMedicalRequestRepository;
		this.patientRepository = patientRepository;
		this.emrPatientMedicalRequestItemRepository = emrPatientMedicalRequestItemRepository;
		this.emrPatientLaboratoryRepository = emrPatientLaboratoryRepository;
		this.emrPatientImagingRepository = emrPatientImagingRepository;
		this.emrPatientProcedureRepository = emrPatientProcedureRepository;
		this.emrMedicalRequestRepository = emrMedicalRequestRepository;
	}

	@GetMapping("/emrPatientMedicalRequest/{patientId}")
	public String viewPatientMedicalRequestByPatientId(Model model, @PathVariable long patientId) {
		Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		Patient patient = optionalPatient.get();
		
		List<EMRPatientLaboratory> emrPatientLaboratoryList = emrPatientLaboratoryRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		List<EMRPatientImaging> emrPatientImagingList = emrPatientImagingRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		List<EMRPatientProcedure> emrPatientProcedureList = emrPatientProcedureRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		
		int totalNewLab = 0;
		int totalNewImaging = 0;
		int totalNewProcedure = 0;
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String today = dateFormatter.format(LocalDateTime.now());

        for(EMRPatientLaboratory obj : emrPatientLaboratoryList) {
        	String dateCreated = dateFormatter.format(obj.getDateCreated());
        	if(dateCreated.equals(today)) {
        		totalNewLab++;
        	}        	
        }
        
        for(EMRPatientImaging obj : emrPatientImagingList) {
        	String dateCreated = dateFormatter.format(obj.getDateCreated());
        	if(dateCreated.equals(today)) {
        		totalNewImaging++;
        	}        	
        }
        
        for(EMRPatientProcedure obj : emrPatientProcedureList) {
        	String dateCreated = dateFormatter.format(obj.getDateCreated());
        	if(dateCreated.equals(today)) {
        		totalNewProcedure++;
        	}        	
        }
        
        patient.setTotalNewLab(totalNewLab);
        patient.setTotalNewImaging(totalNewImaging);
        patient.setTotalNewProcedure(totalNewProcedure);
        
		model.addAttribute("patient", patient);

		List<EMRPatientMedicalRequest> emrPatientMedicalRequestList = emrPatientMedicalRequestRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		

		EMRPatientMedicalRequestForm emrPatientMedicalRequestForm = new EMRPatientMedicalRequestForm();
		List<EMRMedicalRequest> medicalRequestNames = emrMedicalRequestRepository.findAllByDoctorIdOrderByMedicalRequestName(patient.getDoctor().getId());
		
		List<String> medicalRequestLookupList = new ArrayList<String>();
		for(EMRMedicalRequest mr : medicalRequestNames) {
			medicalRequestLookupList.add(mr.getMedicalRequestName());
		}
		
		
		//emrPatientMedicalRequestForm.getEmrPatientMedicalRequestItems().add(new EMRPatientMedicalRequestItem());
		model.addAttribute("medicalRequestLookupList", medicalRequestLookupList);
		model.addAttribute("emrPatientMedicalRequestList", emrPatientMedicalRequestList);	
		model.addAttribute("medicalRequestNames", medicalRequestNames);
		model.addAttribute("emrPatientMedicalRequestForm", emrPatientMedicalRequestForm);		

		return "emr/emr_patient_medical_request";
	}

	@PostMapping("/emrPatientMedicalRequest")
	@Transactional
	public String savePatientMedicalRequest(
			@Valid EMRPatientMedicalRequestForm emrPatientMedicalRequestForm
			,Errors errors
			,final RedirectAttributes redirect
			,Model model
			) {
	    
		if (errors.hasErrors()) {
			model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check Medical Request Names. Medical Request Name cannot be blank."));
			Optional<Patient> optionalPatient = patientRepository.findById(emrPatientMedicalRequestForm.getEmrPatientMedicalRequest().getPatient().getId());
			Patient patient = optionalPatient.get();
			model.addAttribute("patient", patient);

			List<EMRPatientMedicalRequest> emrPatientMedicalRequestList = emrPatientMedicalRequestRepository.findByPatientIdOrderByDateCreatedDesc(patient.getId());
			
			List<EMRMedicalRequest> medicalRequestNames = emrMedicalRequestRepository.findAllByDoctorIdOrderByMedicalRequestName(patient.getDoctor().getId());
			
			List<String> medicalRequestLookupList = new ArrayList<String>();
			for(EMRMedicalRequest mr : medicalRequestNames) {
				medicalRequestLookupList.add(mr.getMedicalRequestName());
			}

//			model.addAttribute("emrPatientMedicalRequestList", emrPatientMedicalRequestList);
//			model.addAttribute("emrPatientMedicalRequestForm", emrPatientMedicalRequestForm);
//			model.addAttribute("emrPatientMedicalRequestForm", emrPatientMedicalRequestForm);
			
			model.addAttribute("medicalRequestLookupList", medicalRequestLookupList);
			model.addAttribute("emrPatientMedicalRequestList", emrPatientMedicalRequestList);	
			model.addAttribute("medicalRequestNames", medicalRequestNames);
			model.addAttribute("emrPatientMedicalRequestForm", emrPatientMedicalRequestForm);
			return "emr/emr_patient_medical_request";
		}

		List<EMRPatientMedicalRequestItem> itemList = new ArrayList<EMRPatientMedicalRequestItem>();
		itemList.addAll(emrPatientMedicalRequestForm.getEmrPatientMedicalRequestItems());
		
		for (EMRPatientMedicalRequestItem medItem : itemList) {
            if (medItem.getRequestName().equalsIgnoreCase("requestName")) {
            	emrPatientMedicalRequestForm.getEmrPatientMedicalRequestItems().remove(medItem);
            }
        }        
       
		EMRPatientMedicalRequest emrPatientMedicalRequest = emrPatientMedicalRequestForm.getEmrPatientMedicalRequest();
		emrPatientMedicalRequest.setEmrPatientMedicalRequestItems(emrPatientMedicalRequestForm.getEmrPatientMedicalRequestItems());

		for (EMRPatientMedicalRequestItem item : emrPatientMedicalRequest.getEmrPatientMedicalRequestItems()) {
			item.setEmrPatientMedicalRequest(emrPatientMedicalRequest);	    
		}

		emrPatientMedicalRequestItemRepository.deleteByEMRPatientMedicalRequestId(emrPatientMedicalRequest.getId());

		emrPatientMedicalRequestRepository.save(emrPatientMedicalRequest);
		redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Medical Request added successfully."));
		return "redirect:/emrPatientMedicalRequest/" + emrPatientMedicalRequestForm.getEmrPatientMedicalRequest().getPatient().getId();
	}

}
