package com.cdsi.emr.reports;

import java.io.File;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cdsi.emr.clinic.Clinic;
import com.cdsi.emr.clinic.ClinicRepository;
import com.cdsi.emr.consultation.EmrConsultation;
import com.cdsi.emr.consultation.EmrConsultationRepository;
import com.cdsi.emr.fileupload.FileStorageProperties;
import com.cdsi.emr.medicalreports.EMRMedicalCertificate;
import com.cdsi.emr.medicalreports.EMRMedicalCertificateRepository;
import com.cdsi.emr.medicalrequest.EMRMedicalRequest;
import com.cdsi.emr.medicalrequest.EMRMedicalRequestRepository;
import com.cdsi.emr.medicalrequest.EMRPatientMedicalRequest;
import com.cdsi.emr.medicalrequest.EMRPatientMedicalRequestItem;
import com.cdsi.emr.medicalrequest.EMRPatientMedicalRequestRepository;
import com.cdsi.emr.medication.EMRPatientMedication;
import com.cdsi.emr.medication.EMRPatientMedicationItem;
import com.cdsi.emr.medication.EMRPatientMedicationRepository;
import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;
import com.cdsi.emr.personnel.Personnel;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
public class ReportsController {
	
	public static final String EMR_RX_LOGO_URL = "/resources/static/images/rx.jpg";
    
	private EMRPatientMedicationRepository emrPatientMedicationRepository;	
	private PatientRepository patientRepository;
	private ClinicRepository clinicRepository;
	private EMRMedicalCertificateRepository emrMedicalCertificateRepository;
	private FileStorageProperties fileStorageProperties;
	private EMRPatientMedicalRequestRepository emrPatientMedicalRequestRepository;
	private EMRMedicalRequestRepository emrMedicalRequestRepository;
	private EmrConsultationRepository emrConsultationRepository;
	
	public ReportsController (PatientRepository patientRepository, 
			EMRPatientMedicationRepository emrPatientMedicationRepository,
			ClinicRepository clinicRepository,
			EMRMedicalCertificateRepository emrMedicalCertificateRepository,
			FileStorageProperties fileStorageProperties,
			EMRPatientMedicalRequestRepository emrPatientMedicalRequestRepository,
			EMRMedicalRequestRepository emrMedicalRequestRepository,
			EmrConsultationRepository emrConsultationRepository) {
	    	
		this.emrPatientMedicationRepository = emrPatientMedicationRepository;		
		this.patientRepository = patientRepository;
		this.clinicRepository = clinicRepository;
		this.emrMedicalCertificateRepository = emrMedicalCertificateRepository;
		this.fileStorageProperties = fileStorageProperties;
		this.emrPatientMedicalRequestRepository = emrPatientMedicalRequestRepository;
		this.emrMedicalRequestRepository = emrMedicalRequestRepository;
		this.emrConsultationRepository = emrConsultationRepository;
		
	}
	
//	@GetMapping("/viewPrescription/{medicationId}")
//	public void listAll(Model model, @PathVariable long medicationId, Authentication auth, HttpServletRequest request, HttpServletResponse response) throws JRException, Exception {
//		
//		Personnel doctor = (Personnel) auth.getPrincipal();
//		
//		Optional<EMRPatientMedication> oEMRPatientMedication = emrPatientMedicationRepository.findById(medicationId);
//		EMRPatientMedication emrPatientMedication = oEMRPatientMedication.orElseGet(() -> new EMRPatientMedication());
//		
//		Optional<Patient> oPatient = patientRepository.findById(emrPatientMedication.getPatient().getId());
//		Patient patient = oPatient.orElseGet(() -> new Patient());
//		
//		List<Clinic> clinicList = clinicRepository.findAllByDoctorId(doctor.getId());
//		
//		String docLogo = doctor.getClinicLogoUrl();
//		
//		File file = ResourceUtils.getFile("classpath:static/images/rx.jpg");
//		File cdsiFile = ResourceUtils.getFile("classpath:static/images/poweredBy.png");
//		
//		String rxLogo = file.getAbsolutePath();
//		String cdsiLogo = cdsiFile.getAbsolutePath();
////		String hospitalLogo = fileStorageProperties.getUploadDir() + docLogo.substring(docLogo.lastIndexOf("/"));	
//		
//		String hospitalLogo = "";
//		
//		if(docLogo != null) {
//			hospitalLogo = fileStorageProperties.getUploadDir() + docLogo.substring(docLogo.lastIndexOf("/"));
//		}
//				
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("RX_LOGO", rxLogo);
//		map.put("CDSI_LOGO", cdsiLogo);
//		map.put("COMPANY_LOGO", hospitalLogo);
//		map.put("DOCTOR_NAME", doctor.getFirstName() + " " + doctor.getLastName());
//		map.put("CREDENTIALS", doctor.getCredentials() != null ? doctor.getCredentials() : "");
//		map.put("SPECIALIZATION", doctor.getSpecialization() != null ? doctor.getSpecialization() : "");
//		
//		map.put("CLINIC_NAME", "");
//		map.put("DOCTOR_ADDRESS", "");
//		map.put("DOCTOR_CONTACT_NO", "");	
//		
//		map.put("CLINIC_NAME2", "");
//		map.put("DOCTOR_ADDRESS2", "");
//		map.put("DOCTOR_CONTACT_NO2", "");
//		
//		int ctr = 1;
//		
//		for(Clinic clinic : clinicList){
//			
//			if(ctr == 1){
//				map.put("CLINIC_NAME", clinic.getName());
//				map.put("DOCTOR_ADDRESS", clinic.getAddress());
//				map.put("DOCTOR_ADDRESS", clinic.getScheduleRx());
//				map.put("DOCTOR_CONTACT_NO", "Contact No: " + clinic.getContactNumber());				
//			} else if (ctr == 2) {
//				map.put("CLINIC_NAME2", clinic.getName());
//				map.put("DOCTOR_ADDRESS2", clinic.getAddress());
//				map.put("DOCTOR_ADDRESS2", clinic.getScheduleRx());
//				map.put("DOCTOR_CONTACT_NO2", "Contact No: " + clinic.getContactNumber());			
//			} else {
//				break;
//			}
//			
//			ctr++;
//		}
//		
//		map.put("DOCTOR_LICENSE_NO", doctor.getLicenseNumber());
//		map.put("DOCTOR_PTR_NO", doctor.getPtrNumber() != null ? doctor.getPtrNumber() : "");
//		map.put("DOCTOR_S_NO", doctor.getSNumber() != null ? doctor.getSNumber() : "");
//		
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
//		map.put("PERIOD", formatter.format(emrPatientMedication.getDateCreated()));
//		
//		map.put("PATIENT_NAME", patient.getLastName() + ", " + patient.getFirstName());
//		map.put("PATIENT_ADDRESS", patient.getStreet()  != null ? patient.getStreet() : "" + " " + patient.getCity());
//		map.put("PATIENT_GENDER", patient.getGender());		
//		map.put("PATIENT_AGE", patient.getAgeStr());
//		
//		ctr = 1;
//		
//		for(EMRPatientMedicationItem medItem : emrPatientMedication.getEmrPatientMedicationItems()) {
//			String medicine = "";
//			medicine = medItem.getGenericName() != null ? medItem.getGenericName() : "";
//			medicine = medicine + (medItem.getBrandName() != null && !medItem.getBrandName().isEmpty() ? " ( " + medItem.getBrandName() + " ) " : "");
//			medicine = medicine + (medItem.getDosage() != null && !medItem.getDosage().isEmpty() ? " - " + medItem.getDosage() : "");
//			medicine = medicine + (medItem.getUnitOfMeasure() != null && !medItem.getUnitOfMeasure().isEmpty() ? " " + medItem.getUnitOfMeasure() : "");
//			
//			map.put("MEDICATION"+ctr, medicine);
//			map.put("INSTRUCTION"+ctr, medItem.getRemarks() != null ? medItem.getRemarks() : "");
//			ctr++;
//		}
//		
//		
//		
//		
//		List<Patient> dataList = new ArrayList<Patient>();	
//		
//		Patient dummyData = new Patient();
//		dummyData.setFirstName("test");
//		
//		dataList.add(dummyData);
//		
//		
//		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataList);
//		
//		response.setContentType("application/pdf");
//		
////		InputStream reportStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "jasper/reports/PrescriptionReport.jasper");
//		InputStream reportStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "jasper/reports/PrescriptionReportV2P1.jasper");
//		
//		//if(emrPatientMedication.getEmrPatientMedicationItems() != null && emrPatientMedication.getEmrPatientMedicationItems().size() > 3){
//		if(emrPatientMedication.getEmrPatientMedicationItems() != null && emrPatientMedication.getEmrPatientMedicationItems().size() > 6){
////			reportStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "jasper/reports/PrescriptionReport2Page.jasper");
//			reportStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "jasper/reports/PrescriptionReportV2P2.jasper");
//		}		
//		
//		if(reportStream == null){
//			System.out.println("reportStream is NULL");
//		}
//		
//		if(response.getOutputStream() == null){
//			System.out.println("response.getOutputStream() is NULL");
//		}
//		
//		JasperRunManager.runReportToPdfStream(reportStream,	response.getOutputStream(), map, beanColDataSource);
//		
//	}	

	@GetMapping("/viewPrescription/{medicationId}")
	public void listAll(Model model, @PathVariable long medicationId, Authentication auth, HttpServletRequest request, HttpServletResponse response) throws JRException, Exception {
	    
	    Personnel doctor = (Personnel) auth.getPrincipal();
	    Optional<EMRPatientMedication> oEMRPatientMedication = emrPatientMedicationRepository.findById(medicationId);
	    EMRPatientMedication emrPatientMedication = oEMRPatientMedication.orElseGet(() -> new EMRPatientMedication());
	    Patient patient = emrPatientMedication.getPatient();
	    List<Clinic> clinicList = clinicRepository.findAllByDoctorId(doctor.getId());
	    
	    Map<String, Object> map = new HashMap<String, Object>();
	    
	    // --- DOCTOR DATA ---
	    map.put("DOCTOR_NAME", doctor.getFirstName() + " " + doctor.getLastName());
	    map.put("CREDENTIALS2", doctor.getCredentials() != null ? doctor.getCredentials() : "");
	    map.put("SPECIALIZATION", doctor.getSpecialization() != null ? doctor.getSpecialization() : "");
	    map.put("DOCTOR_LICENSE_NO", doctor.getLicenseNumber());
	    map.put("DOCTOR_PTR_NO", doctor.getPtrNumber() != null ? doctor.getPtrNumber() : "");
	    map.put("DOCTOR_S_NO", doctor.getSNumber() != null ? doctor.getSNumber() : "");

	    // --- CLINIC DATA (Mirror Fix for Clinic #2) ---
	    map.put("CLINIC_NAME", ""); map.put("DOCTOR_ADDRESS", "");
	    map.put("CLINIC_NAME2", ""); map.put("DOCTOR_ADDRESS2", "");
	    int ctr = 1;
	    for(Clinic clinic : clinicList){
	        String addr = (clinic.getAddress() != null ? clinic.getAddress() : "") 
	        		+ (clinic.getScheduleRx() != null ? "\n" + clinic.getScheduleRx() : "")
	        		+ (clinic.getContactNumber() != null ? "\nContact: " + clinic.getContactNumber() : "");
	        		
	        if(ctr == 1){
	            map.put("CLINIC_NAME", clinic.getName());
	            map.put("DOCTOR_ADDRESS", addr);
	        } else if (ctr == 2) {
	            map.put("CLINIC_NAME2", clinic.getName());
	            map.put("DOCTOR_ADDRESS2", addr);
	        }
	        ctr++;
	    }

	    // --- PATIENT DATA (Fixes 'null' labels) ---
	    map.put("PATIENT_NAME", patient.getLastName() + ", " + patient.getFirstName());
	    map.put("PATIENT_ADDRESS", (patient.getStreet() != null ? patient.getStreet() : "") + " " + (patient.getCity() != null ? patient.getCity() : ""));
	    map.put("PATIENT_GENDER", patient.getGender());
	    map.put("PATIENT_AGE", patient.getAgeStr());
	    map.put("PERIOD", DateTimeFormatter.ofPattern("MMMM dd, yyyy").format(emrPatientMedication.getDateCreated()));

	    // --- LOGOS ---
//	    map.put("RX_LOGO", ResourceUtils.getFile("classpath:static/images/rx.jpg").getAbsolutePath());
//	    map.put("CDSI_LOGO", ResourceUtils.getFile("classpath:static/images/poweredBy.png").getAbsolutePath());

	    
	    
	    // --- FETCH DIAGNOSIS ---
	    String diagnosisText = "";
	    List<EmrConsultation> consults = emrConsultationRepository.findAllByPatientIdOrderByConsultationDateDesc(patient.getId());
	    java.time.LocalDate targetDate = emrPatientMedication.getDateCreated().toLocalDate();
	    for (EmrConsultation consult : consults) {
	        if (consult.getConsultationDate().equals(targetDate) && consult.getPersonnel().getId() == doctor.getId()) {
	            diagnosisText = (consult.getDiagnosisTxt() != null) ? consult.getDiagnosisTxt() : "";
	            break; 
	        }
	    }

	    // --- MEDICATION SPLIT LOGIC ---
	 // --- MEDICATION SPLIT LOGIC (Restored with Brand, Dosage, and Units) ---
	    StringBuilder med1 = new StringBuilder();
	    StringBuilder med2 = new StringBuilder();
	    List<EMRPatientMedicationItem> items = emrPatientMedication.getEmrPatientMedicationItems();
	    
	    for (int i = 0; i < items.size(); i++) {
	        EMRPatientMedicationItem medItem = items.get(i);
	        StringBuilder current = (i < 5) ? med1 : med2;
	        
	        // Build the detailed medicine string (from the old version's logic)
	        StringBuilder medicineDetail = new StringBuilder();
	        medicineDetail.append(medItem.getGenericName() != null ? medItem.getGenericName() : "");
	        
	        if (medItem.getBrandName() != null && !medItem.getBrandName().isEmpty()) {
	            medicineDetail.append(" ( ").append(medItem.getBrandName()).append(" ) ");
	        }
	        if (medItem.getDosage() != null && !medItem.getDosage().isEmpty()) {
	            medicineDetail.append(" - ").append(medItem.getDosage());
	        }
	        if (medItem.getUnitOfMeasure() != null && !medItem.getUnitOfMeasure().isEmpty()) {
	            medicineDetail.append(" ").append(medItem.getUnitOfMeasure());
	        }

	        // Append to the specific column (med1 or med2)
	        current.append(i + 1).append(".  ").append(medicineDetail.toString());
	        current.append("\n      ").append(medItem.getRemarks() != null ? medItem.getRemarks() : "").append("\n\n");
	    }

	    // --- DIAGNOSIS APPENDING (Follows last medicine) ---
	    if (!diagnosisText.isEmpty()) {
	        StringBuilder lastBlock = (items.size() > 5) ? med2 : med1;
	        lastBlock.append("\n");
	        lastBlock.append("Diagnosis:      ").append(diagnosisText.trim());
	    }

	    map.put("MEDICATION1", med1.toString());
	    map.put("MEDICATION2", med2.toString());
	    
	 // 1. IMPROVED LOGO LOADING
        // Try to load logos; if null, the report will just show a blank space instead of crashing
        map.put("RX_LOGO", getClass().getResourceAsStream("/static/images/rx.jpg"));
        map.put("CDSI_LOGO", getClass().getResourceAsStream("/static/images/poweredBy.png"));

	    // --- TEMPLATE SELECTION & COMPILATION ---
//	    String reportPath = (items.size() > 5) ? "jasper/PrescriptionReportV2P2.jrxml" : "jasper/PrescriptionReportV2P1.jrxml";
//
//	    InputStream reportStream = getClass().getClassLoader().getResourceAsStream(reportPath);
//	    JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
//	    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(new ArrayList<>(java.util.Arrays.asList(new Patient()))));
//	    
//	    response.setContentType("application/pdf");
//	    JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
	    
     // 1. DATA DIAGNOSTICS (Check if 'items' is the problem)
        System.out.println("=================================================");
        System.out.println("       STEP 1: DATA SOURCE CHECK                 ");
        System.out.println("=================================================");
        System.out.println("Item Count: " + (items != null ? items.size() : "NULL!!"));
        if (items != null && items.size() > 0) {
            System.out.println("First Item Generic Name: " + items.get(0).getGenericName());
        }

        // 2. FILE DIAGNOSTICS (You said this is successful now)
        String reportPath = (items.size() > 5) ? "/jasper/PrescriptionReportV2P2.jrxml" : "/jasper/PrescriptionReportV2P1.jrxml";
        InputStream reportStream = getClass().getResourceAsStream(reportPath);

        System.out.println("=================================================");
        System.out.println("       STEP 2: COMPILATION START                 ");
        System.out.println("=================================================");
        System.out.println("Compiling: " + reportPath);

        try {
            // 3. COMPILE SOURCE
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            System.out.println(">>> SUCCESS: Compilation Complete");

            // 4. FILL REPORT
            System.out.println("=================================================");
            System.out.println("       STEP 3: FILLING REPORT                    ");
            System.out.println("=================================================");
            JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(items);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, beanColDataSource);
            System.out.println(">>> SUCCESS: Report Filled");

            // 5. EXPORT
            System.out.println("=================================================");
            System.out.println("       STEP 4: EXPORTING TO PDF                  ");
            System.out.println("=================================================");
            response.setContentType("application/pdf");
            // This header helps some browsers handle the stream better
            response.setHeader("Content-Disposition", "inline; filename=prescription.pdf");
            
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
            response.getOutputStream().flush(); // Ensure data is actually sent
            
            System.out.println(">>> SUCCESS: PDF Streamed to Response");
            System.out.println("=================================================");

        } catch (Exception e) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("ERROR AT STEP: " + e.getStackTrace()[0].getLineNumber());
            System.out.println("ERROR TYPE: " + e.getClass().getName());
            System.out.println("ERROR MSG: " + e.getMessage());
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            e.printStackTrace();
            throw e;
        }
	}
	
	
	@GetMapping("/viewMedicalRequest/{requestId}")
	public void viewMedicalRequest(Model model, @PathVariable long requestId, Authentication auth, HttpServletRequest request, HttpServletResponse response) throws JRException, Exception {
		
		Personnel doctor = (Personnel) auth.getPrincipal();
		
		Optional<EMRPatientMedicalRequest> oEMRPatientMedicalRequest = emrPatientMedicalRequestRepository.findById(requestId);
		EMRPatientMedicalRequest emrPatientMedicalRequest = oEMRPatientMedicalRequest.orElseGet(() -> new EMRPatientMedicalRequest());
		
		Optional<Patient> oPatient = patientRepository.findById(emrPatientMedicalRequest.getPatient().getId());
		Patient patient = oPatient.orElseGet(() -> new Patient());
		
		List<Clinic> clinicList = clinicRepository.findAllByDoctorId(doctor.getId());
		
		String docLogo = doctor.getClinicLogoUrl();
		
		//File file = ResourceUtils.getFile("classpath:static/images/rx.jpg");
		File cdsiFile = ResourceUtils.getFile("classpath:static/images/poweredBy.png");
		
		//String rxLogo = file.getAbsolutePath();
		String cdsiLogo = cdsiFile.getAbsolutePath();
//		String hospitalLogo = fileStorageProperties.getUploadDir() + docLogo.substring(docLogo.lastIndexOf("/"));	
		
//		String hospitalLogo = "";
//		
//		if(docLogo != null) {
//			hospitalLogo = fileStorageProperties.getUploadDir() + docLogo.substring(docLogo.lastIndexOf("/"));
//		}
				
		Map<String, Object> map = new HashMap<String, Object>();
		//map.put("RX_LOGO", rxLogo);
		//map.put("CDSI_LOGO", getClass().getResourceAsStream("/static/images/poweredBy.png"));
		
		// Use this pattern to ensure it finds the resource inside the JAR
		InputStream rxStream = getClass().getResourceAsStream("/static/images/rx.jpg");
		if (rxStream == null) {
		    rxStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("static/images/rx.jpg");
		}
		map.put("RX_LOGO", rxStream);

		InputStream cdsiStream = getClass().getResourceAsStream("/static/images/poweredBy.png");
		if (cdsiStream == null) {
		    cdsiStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("static/images/poweredBy.png");
		}
		map.put("CDSI_LOGO", cdsiStream);
		
//		map.put("COMPANY_LOGO", hospitalLogo);
		
		// CURRENT (Broken in JAR/Railway):
		// hospitalLogo = fileStorageProperties.getUploadDir() + docLogo.substring(docLogo.lastIndexOf("/"));

		// FIXED:
		if (docLogo != null && !docLogo.isEmpty()) {
		    // If docLogo is a URL or relative path, it's better to load it as a stream
		    // For now, ensure your 'file.upload-dir' in application-prod.properties 
		    // points to a valid Railway Volume path like /app/uploads
		    map.put("COMPANY_LOGO", fileStorageProperties.getUploadDir() + docLogo.substring(docLogo.lastIndexOf("/")));
		}
		
		map.put("DOCTOR_NAME", doctor.getFirstName() + " " + doctor.getLastName());
		map.put("CREDENTIALS", doctor.getCredentials() != null ? doctor.getCredentials() : "");
		map.put("SPECIALIZATION", doctor.getSpecialization() != null ? doctor.getSpecialization() : "");
		
		map.put("CLINIC_NAME", "");
		map.put("DOCTOR_ADDRESS", "");
		map.put("DOCTOR_CONTACT_NO", "");	
		
		map.put("CLINIC_NAME2", "");
		map.put("DOCTOR_ADDRESS2", "");
		map.put("DOCTOR_CONTACT_NO2", "");
		
		int ctr = 1;
		
		for(Clinic clinic : clinicList){
			
			if(ctr == 1){
				map.put("CLINIC_NAME", clinic.getName());
				map.put("DOCTOR_ADDRESS", clinic.getAddress());
				map.put("DOCTOR_ADDRESS", clinic.getScheduleRx());
				map.put("DOCTOR_CONTACT_NO", "Contact No: " + clinic.getContactNumber());				
			} else if (ctr == 2) {
				map.put("CLINIC_NAME2", clinic.getName());
				map.put("DOCTOR_ADDRESS2", clinic.getAddress());
				map.put("DOCTOR_ADDRESS2", clinic.getScheduleRx());
				map.put("DOCTOR_CONTACT_NO2", "Contact No: " + clinic.getContactNumber());			
			} else {
				break;
			}
			
			ctr++;
		}
		
		map.put("DOCTOR_LICENSE_NO", doctor.getLicenseNumber());
		map.put("DOCTOR_PTR_NO", doctor.getPtrNumber() != null ? doctor.getPtrNumber() : "");
		map.put("DOCTOR_S_NO", doctor.getSNumber() != null ? doctor.getSNumber() : "");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
		map.put("PERIOD", formatter.format(emrPatientMedicalRequest.getDateCreated()));
		
		map.put("PATIENT_NAME", patient.getLastName() + ", " + patient.getFirstName());
		map.put("PATIENT_ADDRESS", patient.getStreet()  != null ? patient.getStreet() : "" + " " + patient.getCity());
		map.put("PATIENT_GENDER", patient.getGender());		
		map.put("PATIENT_AGE", patient.getAgeStr());
		
				
		StringBuffer medicalRequestItems = new StringBuffer();
		StringBuffer medicalRequestItems2 = new StringBuffer();
		StringBuffer medicalRequestItems3 = new StringBuffer();
		
		medicalRequestItems.append("");
		medicalRequestItems2.append("");
		medicalRequestItems3.append("");
		
//		for(EMRPatientMedicalRequestItem medItem : emrPatientMedicalRequest.getEmrPatientMedicalRequestItems()) {
//			
//			medicalRequestItems.append(medItem.getRequestName() != null ? "- " + medItem.getRequestName() + "\n" : "");		
//			
//		}
		
		int x = 1;
//		for(int x = 1; x <= 30; x++) {		
		for(EMRPatientMedicalRequestItem medItem : emrPatientMedicalRequest.getEmrPatientMedicalRequestItems()) {
			String grpName = "";
			
			if(medItem.getRequestName() != null) {
				EMRMedicalRequest emrMedRqst = emrMedicalRequestRepository.findByMedicalRequestNameAndDoctorId(medItem.getRequestName(), patient.getDoctor().getId());
				if(emrMedRqst != null && emrMedRqst.getEmrMedicalRequestGroup() != null) {
					grpName = " (" + emrMedRqst.getEmrMedicalRequestGroup().getMedicalRequestGroupName() + ") ";
				}
			}
			
			if(x > 10 && x <= 20) {
				//medicalRequestItems2.append(x + ".) Item " + x + "\n");
				medicalRequestItems2.append(x + ".) " + medItem.getRequestName() != null ? x + ".) " + medItem.getRequestName() + " " + grpName + "\n" : "");
			} else if (x > 20) {
				//medicalRequestItems3.append(x + ".) Item " + x + "\n");
				medicalRequestItems3.append(x + ".) " + medItem.getRequestName() != null ? x + ".) " + medItem.getRequestName() + " " + grpName + "\n" : "");
			} else {
				//medicalRequestItems.append(x + ".) Item " + x + "\n");
				medicalRequestItems.append(medItem.getRequestName() != null ? x + ".) " + medItem.getRequestName() + " " + grpName + "\n" : "");		
			}
			x++;
		}
		
		
		map.put("DATE_REQUESTED", formatter.format(emrPatientMedicalRequest.getDateCreated()));
		map.put("PATIENT_REQUEST", medicalRequestItems.toString());
		map.put("PATIENT_REQUEST2", medicalRequestItems2.toString());
		map.put("PATIENT_REQUEST3", medicalRequestItems3.toString());
		map.put("PATIENT_INSTRUCTION", emrPatientMedicalRequest.getInstructions() != null ? emrPatientMedicalRequest.getInstructions() : "");
		map.put("PATIENT_DIAGNOSIS", emrPatientMedicalRequest.getDiagnosis() != null ? emrPatientMedicalRequest.getDiagnosis() : "");
		
		List<Patient> dataList = new ArrayList<Patient>();	
		
		Patient dummyData = new Patient();
		dummyData.setFirstName("test");
		
		dataList.add(dummyData);
		
		
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataList);
		
		response.setContentType("application/pdf");
		
		InputStream reportStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "jasper/reports/MedicalRequestReport.jasper");
		
		
		if(reportStream == null){
			System.out.println("reportStream is NULL");
		}
		
		if(response.getOutputStream() == null){
			System.out.println("response.getOutputStream() is NULL");
		}
		
		JasperRunManager.runReportToPdfStream(reportStream,	response.getOutputStream(), map, beanColDataSource);
		
	}
	
//	@GetMapping("/viewMedCert/{medCertId}")
//	public void listAllMedCert(Model model, @PathVariable long medCertId, Authentication auth, HttpServletRequest request, HttpServletResponse response) throws JRException, Exception {
//		
//		Personnel doctor = (Personnel) auth.getPrincipal();
//		
//		Optional<EMRMedicalCertificate> oEMRMedicalCertificate = emrMedicalCertificateRepository.findById(medCertId);
//		EMRMedicalCertificate emrMedicalCertificate = oEMRMedicalCertificate.orElseGet(() -> new EMRMedicalCertificate());
//		
//		Optional<Patient> oPatient = patientRepository.findById(emrMedicalCertificate.getPatient().getId());
//		Patient patient = oPatient.orElseGet(() -> new Patient());
//		
//		List<Clinic> clinicList = clinicRepository.findAllByDoctorId(doctor.getId());
//		
//		File file = ResourceUtils.getFile("classpath:static/images/rx.jpg");
//		File cdsiFile = ResourceUtils.getFile("classpath:static/images/poweredBy.png");
//		
//		String rxLogo = file.getAbsolutePath();
//		String cdsiLogo = cdsiFile.getAbsolutePath();
////		String hospitalLogo = hospitalLogoFile.getAbsolutePath();
//		
//		String docLogo = doctor.getClinicLogoUrl();
//		String hospitalLogo = "";
//		
//		if(docLogo != null) {
//			hospitalLogo = fileStorageProperties.getUploadDir() + docLogo.substring(docLogo.lastIndexOf("/"));
//		}
//		
//				
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("RX_LOGO", rxLogo);
//		map.put("CDSI_LOGO", cdsiLogo);
//		map.put("COMPANY_LOGO", hospitalLogo);
//		map.put("DOCTOR_NAME", doctor.getFirstName() + " " + doctor.getLastName());
//		map.put("CREDENTIALS", doctor.getCredentials() != null ? doctor.getCredentials() : "");
//		map.put("SPECIALIZATION", doctor.getSpecialization() != null ? doctor.getSpecialization() : "");
//		
//		map.put("CLINIC_NAME", "");
//		map.put("DOCTOR_ADDRESS", "");
//		map.put("DOCTOR_CONTACT_NO", "");	
//		
//		map.put("CLINIC_NAME2", "");
//		map.put("DOCTOR_ADDRESS2", "");
//		map.put("DOCTOR_CONTACT_NO2", "");
//		
//		int ctr = 1;
//		
//		for(Clinic clinic : clinicList){
//			
//			if(ctr == 1){
//				map.put("CLINIC_NAME", clinic.getName());
//				map.put("DOCTOR_ADDRESS", clinic.getAddress());
//				map.put("DOCTOR_CONTACT_NO", clinic.getContactNumber());				
//			} else {
//				break;
//			}
//			
//			ctr++;
//		}
//		
//		map.put("DOCTOR_LICENSE_NO", doctor.getLicenseNumber());
//		map.put("DOCTOR_PTR_NO", doctor.getPtrNumber() != null ? doctor.getPtrNumber() : "" );
//		map.put("DOCTOR_S_NO", doctor.getSNumber() != null ? doctor.getSNumber() : "");
//		
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
//		map.put("PERIOD", formatter.format(emrMedicalCertificate.getDateRequested()));
//		
//		map.put("PATIENT_NAME", patient.getLastName() + ", " + patient.getFirstName());
//		map.put("PATIENT_ADDRESS", patient.getStreet()  != null ? patient.getStreet() : "" + " " + patient.getCity());
//		map.put("PATIENT_GENDER", patient.getGender());		
//		map.put("PATIENT_AGE", patient.getAgeStr() + "");
//		map.put("PATIENT_MEDICATION", emrMedicalCertificate.getMedication());
//		map.put("PATIENT_RECOMMENDATION", emrMedicalCertificate.getRecommendation());
//		map.put("PATIENT_DIAGNOSIS", emrMedicalCertificate.getDiagnosis());
//		map.put("PATIENT_HISTORY", emrMedicalCertificate.getHistory());
//		
//		
//		
//		
//		List<Patient> dataList = new ArrayList<Patient>();	
//		
//		Patient dummyData = new Patient();
//		dummyData.setFirstName("test");
//		
//		dataList.add(dummyData);
//		
//		
//		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataList);
//		
//		response.setContentType("application/pdf");
//		
//		InputStream reportStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "jasper/reports/MedicalCertReport.jasper");
//				
//		if(reportStream == null){
//			//logger.debug("reportStream is NULL");
//		}
//		
//		JasperRunManager.runReportToPdfStream(reportStream,	response.getOutputStream(), map, beanColDataSource);
//		
//	}
	
	@GetMapping("/viewMedCert/{medCertId}")
	public void listAllMedCert(Model model, @PathVariable long medCertId, Authentication auth, HttpServletRequest request, HttpServletResponse response) throws JRException, Exception {
		
		Personnel doctor = (Personnel) auth.getPrincipal();
		
		Optional<EMRMedicalCertificate> oEMRMedicalCertificate = emrMedicalCertificateRepository.findById(medCertId);
		EMRMedicalCertificate emrMedicalCertificate = oEMRMedicalCertificate.orElseGet(() -> new EMRMedicalCertificate());
		
		Optional<Patient> oPatient = patientRepository.findById(emrMedicalCertificate.getPatient().getId());
		Patient patient = oPatient.orElseGet(() -> new Patient());
		
		List<Clinic> clinicList = clinicRepository.findAllByDoctorId(doctor.getId());
		
		File file = ResourceUtils.getFile("classpath:static/images/rx.jpg");
		File cdsiFile = ResourceUtils.getFile("classpath:static/images/poweredBy.png");
		
		String rxLogo = file.getAbsolutePath();
		String cdsiLogo = cdsiFile.getAbsolutePath();
		
		String docLogo = doctor.getClinicLogoUrl();
//		String hospitalLogo = "";
//		
//		if(docLogo != null) {
//			hospitalLogo = fileStorageProperties.getUploadDir() + docLogo.substring(docLogo.lastIndexOf("/"));
//		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		// Use this pattern to ensure it finds the resource inside the JAR
		InputStream rxStream = getClass().getResourceAsStream("/static/images/rx.jpg");
		if (rxStream == null) {
		    rxStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("static/images/rx.jpg");
		}
		map.put("RX_LOGO", rxStream);

		InputStream cdsiStream = getClass().getResourceAsStream("/static/images/poweredBy.png");
		if (cdsiStream == null) {
		    cdsiStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("static/images/poweredBy.png");
		}
		map.put("CDSI_LOGO", cdsiStream);
//		map.put("COMPANY_LOGO", hospitalLogo);\
		
		// FIXED:
		if (docLogo != null && !docLogo.isEmpty()) {
		    // If docLogo is a URL or relative path, it's better to load it as a stream
		    // For now, ensure your 'file.upload-dir' in application-prod.properties 
		    // points to a valid Railway Volume path like /app/uploads
		    map.put("COMPANY_LOGO", fileStorageProperties.getUploadDir() + docLogo.substring(docLogo.lastIndexOf("/")));
		}
		map.put("DOCTOR_NAME", doctor.getFirstName() + " " + doctor.getLastName());
		map.put("CREDENTIALS", doctor.getCredentials() != null ? doctor.getCredentials() : "");
		map.put("SPECIALIZATION", doctor.getSpecialization() != null ? doctor.getSpecialization() : "");
		
		map.put("CLINIC_NAME", "");
		map.put("DOCTOR_ADDRESS", "");
		map.put("DOCTOR_CONTACT_NO", "");	
		
		map.put("CLINIC_NAME2", "");
		map.put("DOCTOR_ADDRESS2", "");
		map.put("DOCTOR_CONTACT_NO2", "");
		
		int ctr = 1;
		
		for(Clinic clinic : clinicList){
			if(ctr == 1){
				map.put("CLINIC_NAME", clinic.getName());
				map.put("DOCTOR_ADDRESS", clinic.getAddress());
				map.put("DOCTOR_CONTACT_NO", clinic.getContactNumber());				
			} else {
				break;
			}
			ctr++;
		}
		
		map.put("DOCTOR_LICENSE_NO", doctor.getLicenseNumber());
		map.put("DOCTOR_PTR_NO", doctor.getPtrNumber() != null ? doctor.getPtrNumber() : "" );
		map.put("DOCTOR_S_NO", doctor.getSNumber() != null ? doctor.getSNumber() : "");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
		map.put("PERIOD", formatter.format(emrMedicalCertificate.getDateRequested()));
		
		map.put("PATIENT_NAME", patient.getLastName() + ", " + patient.getFirstName());
		map.put("PATIENT_ADDRESS", (patient.getStreet()  != null ? patient.getStreet() : "") + " " + (patient.getCity() != null ? patient.getCity() : ""));
		map.put("PATIENT_GENDER", patient.getGender());		
		map.put("PATIENT_AGE", patient.getAgeStr() + "");
		map.put("PATIENT_MEDICATION", emrMedicalCertificate.getMedication());
		map.put("PATIENT_RECOMMENDATION", emrMedicalCertificate.getRecommendation());
		map.put("PATIENT_DIAGNOSIS", emrMedicalCertificate.getDiagnosis());
		map.put("PATIENT_HISTORY", emrMedicalCertificate.getHistory());
		
		List<Patient> dataList = new ArrayList<Patient>();	
		Patient dummyData = new Patient();
		dummyData.setFirstName("test");
		dataList.add(dummyData);
		
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataList);
		
		// --- UPDATED TO USE .jrxml AND COMPILATION ---
		response.setContentType("application/pdf");
		
		// Pointing to the .jrxml source file instead of the compiled .jasper
		InputStream reportStream = getClass().getClassLoader().getResourceAsStream("jasper/MedicalCertReport.jrxml");
		
		if(reportStream == null){
			// Log error if file not found
			System.out.println("MedicalCertReport.jrxml not found in classpath");
		} else {
			// 1. Compile the JRXML
			JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
			
			// 2. Fill the report with data
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, beanColDataSource);
			
			// 3. Export to the response output stream
			JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		}
	}
	
}
