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
import com.cdsi.emr.medicalreports.EMRMedicalCertificate;
import com.cdsi.emr.medicalreports.EMRMedicalCertificateRepository;
import com.cdsi.emr.medicalrequest.EMRMedicalRequest;
import com.cdsi.emr.medicalrequest.EMRMedicalRequestRepository;
import com.cdsi.emr.medicalrequest.EMRPatientMedicalRequest;
import com.cdsi.emr.medicalrequest.EMRPatientMedicalRequestItem;
import com.cdsi.emr.medicalrequest.EMRPatientMedicalRequestRepository;
import com.cdsi.emr.clinic.Clinic;
import com.cdsi.emr.clinic.ClinicRepository;
import com.cdsi.emr.fileupload.FileStorageProperties;
import com.cdsi.emr.medication.EMRPatientMedication;
import com.cdsi.emr.medication.EMRPatientMedicationItem;
import com.cdsi.emr.medication.EMRPatientMedicationRepository;
import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;
import com.cdsi.emr.personnel.Personnel;
import net.sf.jasperreports.engine.JRException;
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
	
	public ReportsController (PatientRepository patientRepository, 
			EMRPatientMedicationRepository emrPatientMedicationRepository,
			ClinicRepository clinicRepository,
			EMRMedicalCertificateRepository emrMedicalCertificateRepository,
			FileStorageProperties fileStorageProperties,
			EMRPatientMedicalRequestRepository emrPatientMedicalRequestRepository,
			EMRMedicalRequestRepository emrMedicalRequestRepository) {
	    	
		this.emrPatientMedicationRepository = emrPatientMedicationRepository;		
		this.patientRepository = patientRepository;
		this.clinicRepository = clinicRepository;
		this.emrMedicalCertificateRepository = emrMedicalCertificateRepository;
		this.fileStorageProperties = fileStorageProperties;
		this.emrPatientMedicalRequestRepository = emrPatientMedicalRequestRepository;
		this.emrMedicalRequestRepository = emrMedicalRequestRepository;
		
	}
	
	@GetMapping("/viewPrescription/{medicationId}")
	public void listAll(Model model, @PathVariable long medicationId, Authentication auth, HttpServletRequest request, HttpServletResponse response) throws JRException, Exception {
		
		Personnel doctor = (Personnel) auth.getPrincipal();
		
		Optional<EMRPatientMedication> oEMRPatientMedication = emrPatientMedicationRepository.findById(medicationId);
		EMRPatientMedication emrPatientMedication = oEMRPatientMedication.orElseGet(() -> new EMRPatientMedication());
		
		Optional<Patient> oPatient = patientRepository.findById(emrPatientMedication.getPatient().getId());
		Patient patient = oPatient.orElseGet(() -> new Patient());
		
		List<Clinic> clinicList = clinicRepository.findAllByDoctorId(doctor.getId());
		
		String docLogo = doctor.getClinicLogoUrl();
		
		File file = ResourceUtils.getFile("classpath:static/images/rx.jpg");
		File cdsiFile = ResourceUtils.getFile("classpath:static/images/poweredBy.png");
		
		String rxLogo = file.getAbsolutePath();
		String cdsiLogo = cdsiFile.getAbsolutePath();
//		String hospitalLogo = fileStorageProperties.getUploadDir() + docLogo.substring(docLogo.lastIndexOf("/"));	
		
		String hospitalLogo = "";
		
		if(docLogo != null) {
			hospitalLogo = fileStorageProperties.getUploadDir() + docLogo.substring(docLogo.lastIndexOf("/"));
		}
				
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("RX_LOGO", rxLogo);
		map.put("CDSI_LOGO", cdsiLogo);
		map.put("COMPANY_LOGO", hospitalLogo);
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
		map.put("PERIOD", formatter.format(emrPatientMedication.getDateCreated()));
		
		map.put("PATIENT_NAME", patient.getLastName() + ", " + patient.getFirstName());
		map.put("PATIENT_ADDRESS", patient.getStreet()  != null ? patient.getStreet() : "" + " " + patient.getCity());
		map.put("PATIENT_GENDER", patient.getGender());		
		map.put("PATIENT_AGE", patient.getAgeStr());
		
		ctr = 1;
		
		for(EMRPatientMedicationItem medItem : emrPatientMedication.getEmrPatientMedicationItems()) {
			String medicine = "";
			medicine = medItem.getGenericName() != null ? medItem.getGenericName() : "";
			medicine = medicine + (medItem.getBrandName() != null && !medItem.getBrandName().isEmpty() ? " ( " + medItem.getBrandName() + " ) " : "");
			medicine = medicine + (medItem.getDosage() != null && !medItem.getDosage().isEmpty() ? " - " + medItem.getDosage() : "");
			
			map.put("MEDICATION"+ctr, medicine);
			map.put("INSTRUCTION"+ctr, medItem.getRemarks() != null ? medItem.getRemarks() : "");
			ctr++;
		}
		
		
		
		
		List<Patient> dataList = new ArrayList<Patient>();	
		
		Patient dummyData = new Patient();
		dummyData.setFirstName("test");
		
		dataList.add(dummyData);
		
		
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataList);
		
		response.setContentType("application/pdf");
		
		InputStream reportStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "jasper/reports/PrescriptionReport.jasper");
		
		if(emrPatientMedication.getEmrPatientMedicationItems() != null && emrPatientMedication.getEmrPatientMedicationItems().size() > 3){
			reportStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "jasper/reports/PrescriptionReport2Page.jasper");
		}		
		
		if(reportStream == null){
			System.out.println("reportStream is NULL");
		}
		
		if(response.getOutputStream() == null){
			System.out.println("response.getOutputStream() is NULL");
		}
		
		JasperRunManager.runReportToPdfStream(reportStream,	response.getOutputStream(), map, beanColDataSource);
		
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
		
		String hospitalLogo = "";
		
		if(docLogo != null) {
			hospitalLogo = fileStorageProperties.getUploadDir() + docLogo.substring(docLogo.lastIndexOf("/"));
		}
				
		Map<String, Object> map = new HashMap<String, Object>();
		//map.put("RX_LOGO", rxLogo);
		map.put("CDSI_LOGO", cdsiLogo);
		map.put("COMPANY_LOGO", hospitalLogo);
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
//		String hospitalLogo = hospitalLogoFile.getAbsolutePath();
		
		String docLogo = doctor.getClinicLogoUrl();
		String hospitalLogo = "";
		
		if(docLogo != null) {
			hospitalLogo = fileStorageProperties.getUploadDir() + docLogo.substring(docLogo.lastIndexOf("/"));
		}
		
				
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("RX_LOGO", rxLogo);
		map.put("CDSI_LOGO", cdsiLogo);
		map.put("COMPANY_LOGO", hospitalLogo);
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
		map.put("PATIENT_ADDRESS", patient.getStreet()  != null ? patient.getStreet() : "" + " " + patient.getCity());
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
		
		response.setContentType("application/pdf");
		
		InputStream reportStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "jasper/reports/MedicalCertReport.jasper");
				
		if(reportStream == null){
			//logger.debug("reportStream is NULL");
		}
		
		JasperRunManager.runReportToPdfStream(reportStream,	response.getOutputStream(), map, beanColDataSource);
		
	}
	
	
}
