package com.cdsi.emr.labs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cdsi.emr.fileupload.FileDTO;
import com.cdsi.emr.fileupload.FileInputInitialPreviewConfig;
import com.cdsi.emr.fileupload.FileInputResponse;
import com.cdsi.emr.fileupload.StorageService;
import com.cdsi.emr.imaging.EMRPatientImaging;
import com.cdsi.emr.imaging.EMRPatientImagingRepository;
import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;
import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.procedures.EMRPatientProcedure;
import com.cdsi.emr.procedures.EMRPatientProcedureRepository;
import com.cdsi.emr.util.UXMessage;

@Controller
public class EMRPatientLaboratoryController {
    private StorageService storageService;
    private EMRPatientLaboratoryRepository emrPatientLaboratoryRepository;
    private EMRPatientLaboratoryTypeRepository emrPatientLaboratoryTypeRepository;
    private PatientRepository patientRepository;
    private EMRPatientImagingRepository emrPatientImagingRepository;
	private EMRPatientProcedureRepository emrPatientProcedureRepository;

    public EMRPatientLaboratoryController (
            EMRPatientLaboratoryRepository emrPatientLaboratoryRepository,
            EMRPatientLaboratoryTypeRepository emrPatientLaboratoryTypeRepository,
            PatientRepository patientRepository,
            StorageService storageService
            ,EMRPatientImagingRepository emrPatientImagingRepository
    		,EMRPatientProcedureRepository emrPatientProcedureRepository
            ) {
        this.storageService = storageService;
        this.emrPatientLaboratoryRepository = emrPatientLaboratoryRepository;
        this.emrPatientLaboratoryTypeRepository = emrPatientLaboratoryTypeRepository;
        this.patientRepository = patientRepository;
        this.emrPatientImagingRepository = emrPatientImagingRepository;
		this.emrPatientProcedureRepository = emrPatientProcedureRepository;
    }

    @GetMapping("/patientLaboratory/{admissionId}")
    public String listAllLabByAdmissionId(Model model, @PathVariable long admissionId) {

        //Change This
        List<EMRPatientLaboratory> emrPatientLaboratoryList = this.emrPatientLaboratoryRepository.findByPatientIdOrderByDateCreatedDesc(admissionId);
        Optional<Patient> optionalPatient = this.patientRepository.findById(admissionId);
        Patient patient = optionalPatient.get();


        model.addAttribute("patient", patient);
        model.addAttribute("emrPatientLaboratoryList", emrPatientLaboratoryList);
        model.addAttribute(new EMRPatientLaboratory());
        return "nursestation/ehr_patient_lab";
    }

    @GetMapping("/emrpatientLaboratory/{patientId}")
    public String listAll(Model model, @PathVariable long patientId
            ,@AuthenticationPrincipal Personnel doctor
            ) {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
		Patient patient = optionalPatient.get();
		
		List<EMRPatientLaboratory> emrPatientLaboratoryList = emrPatientLaboratoryRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		List<EMRPatientImaging> emrPatientImagingList = this.emrPatientImagingRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		List<EMRPatientProcedure> emrPatientProcedureList = emrPatientProcedureRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
		
		int totalNewLab = 0;
		int totalNewImaging = 0;
		int totalNewProcedure = 0;
		
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
        
        
        List<EMRPatientLaboratoryType> emrPatientLaboratoryTypeList = this.emrPatientLaboratoryTypeRepository.findAllByDoctorId(doctor.getId());
        
        model.addAttribute("patient", patient);
        model.addAttribute("emrPatientLaboratoryList", emrPatientLaboratoryList);
        model.addAttribute("allLaboratoryTypes", emrPatientLaboratoryTypeList);

        Map<Long, Integer> typeMap = new HashMap<>();
        Map<Integer, EMRPatientLaboratoryType> emrTypeMap = new HashMap<>();
        Map<Integer, List<EMRPatientLaboratory>> patientLabMap = new HashMap<>();

        int index = 0;
        int lastIndex = 0;
        for(EMRPatientLaboratoryType labType : emrPatientLaboratoryTypeList) {
            typeMap.put(labType.getId(), index);
            patientLabMap.put(index, new ArrayList<EMRPatientLaboratory>());
            emrTypeMap.put(index, labType);
            index++;
        }

        lastIndex = index;
        
        for(EMRPatientLaboratory lab : emrPatientLaboratoryList) {        	     	
            if(typeMap.containsKey(lab.getEmrPatientLaboratoryType().getId())) {
                int indexVal = typeMap.get(lab.getEmrPatientLaboratoryType().getId());
                patientLabMap.get(indexVal).add(lab);
            }
        }
        
        patient.setTotalNewLab(totalNewLab);

        List<LaboratoryDisplayDto> dtoList = new ArrayList<>();



        for(int x = 0; x < lastIndex ; x++) {
            String currentDate = "";
            List<EMRPatientLaboratory> plList = patientLabMap.get(x);
            List<String> valueList = new ArrayList<>();
            List<String> timeListAll = new ArrayList<>();
            List<String> timeList = null;
            LaboratoryHeader header = null;
            List<LaboratoryHeader> headerList = new ArrayList<>();



            for(EMRPatientLaboratory pl : plList) {
                String date = dateFormatter.format(pl.getDateCreated());
                timeListAll.add(timeFormatter.format(pl.getDateCreated()));
                if(currentDate.equals(date)) {
                    String time = timeFormatter.format(pl.getDateCreated());
                    timeList.add(time);

                } else {

                    if(header != null) {
                        header.setTimeStr(timeList);
                        header.setColSpan(timeList.size());
                        headerList.add(header);
                    }

                    currentDate = date;
                    header = new LaboratoryHeader();
                    header.setDateStr(date);
                    timeList = new ArrayList<>();

                    String time = timeFormatter.format(pl.getDateCreated());

                    timeList.add(time);

                }

                valueList.add(pl.getLabResult());
            }

            if(header != null) {
                header.setTimeStr(timeList);
                header.setColSpan(timeList.size());
                headerList.add(header);
            }

            LaboratoryDisplayDto dto = new LaboratoryDisplayDto();

            dto.setIndexStr(x+"");
            dto.setLabTypeName(emrTypeMap.get(x).getLabTypeName() != null ? emrTypeMap.get(x).getLabTypeName() : "");
            dto.setLabTypeNormalValue(emrTypeMap.get(x).getLabTypeNormalValue() != null ? emrTypeMap.get(x).getLabTypeNormalValue() : "");
            dto.setTimeListFinal(timeListAll);
            dto.setHeaderListFinal(headerList);
            dto.setValueListFinal(valueList);


            dtoList.add(dto);

        }

        model.addAttribute("dtoList", dtoList);
        model.addAttribute("emrPatientLaboratory", new EMRPatientLaboratory());
        return "emr/emr_patient_lab";
    }

    @PostMapping("/emrpatientLaboratory")
    public String savePatientLaboratory(
            @Valid EMRPatientLaboratory emrPatientLaboratory
            ,Errors errors
            ,final RedirectAttributes redirect
            ,Model model
            ,@AuthenticationPrincipal Personnel doctor
            ) {
        List<String> labFileUrls = new ArrayList<>();
        long patientId = emrPatientLaboratory.getPatient().getId();
        if (errors.hasErrors()  || emrPatientLaboratory.getEmrPatientLaboratoryType().getId() <= 0) {
        	if (emrPatientLaboratory.getEmrPatientLaboratoryType().getId() <= 0) {
                errors.rejectValue("emrPatientLaboratoryType", "emrPatientLaboratoryType.id.zero", " is mandatory");
            }
            model.addAttribute("isValidationErrorOnAdd", true);
        	
            List<EMRPatientLaboratory> emrPatientLaboratoryList = this.emrPatientLaboratoryRepository.findByPatientIdOrderByDateCreatedDesc(patientId);
            List<EMRPatientLaboratoryType> emrPatientLaboratoryTypeList = this.emrPatientLaboratoryTypeRepository.findAllByDoctorId(doctor.getId());
            Optional<Patient> optionalPatient = this.patientRepository.findById(patientId);
            Patient patient = optionalPatient.get();
            model.addAttribute("patient", patient);
            model.addAttribute("emrPatientLaboratoryList", emrPatientLaboratoryList);
            model.addAttribute("allLaboratoryTypes", emrPatientLaboratoryTypeList);
            model.addAttribute("emrPatientLaboratory", emrPatientLaboratory);
            model.addAttribute("uxmessage", new UXMessage("ERROR", "Please check items marked in red."));
            return "emr/emr_patient_lab";
        }

        MultipartFile[] files = emrPatientLaboratory.getLabFiles();
        if(files.length > 0 && files[0].getOriginalFilename().isEmpty()) {
            EMRPatientLaboratory emrImg = this.emrPatientLaboratoryRepository.findById(emrPatientLaboratory.getId())
                    .orElseGet(EMRPatientLaboratory::new);
            labFileUrls = emrImg.getLabFileUrls();
        } else {
            for (int i = 0; i < files.length; i++) {
                try {
                    String fileExt = files[i].getOriginalFilename().substring(files[i].getOriginalFilename().lastIndexOf("."));
                    String fileName = "patient_labfile_" + patientId + '_' + i + "_" + System.currentTimeMillis() + fileExt;
                    FileDTO fileDTO = this.storageService.uploadFile(files[i], fileName);
                    labFileUrls.add(fileDTO.getDownloadUri());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        emrPatientLaboratory.setLabFileUrls(labFileUrls);
        Patient patient = this.patientRepository.findById(patientId).orElseGet(Patient::new);
        emrPatientLaboratory.setPatient(patient);
        this.emrPatientLaboratoryRepository.save(emrPatientLaboratory);

        redirect.addFlashAttribute("uxmessage", new UXMessage("SUCCESS", "Files successfully uploaded."));
        return "redirect:/emrpatientLaboratory/" + patientId;
    }

    @GetMapping("/emrpatientLaboratory/{id}/json")
    public @ResponseBody EMRPatientLaboratory editEmrPatientLab(
            @PathVariable long id
            ) {
        EMRPatientLaboratory lab = this.emrPatientLaboratoryRepository.findById(id)
                .orElseGet(EMRPatientLaboratory::new);
        return lab;
    }

    @PostMapping("/upload/emrpatientLaboratory")
    @Transactional
    public ResponseEntity<FileInputResponse> saveEmrPatientLaboratory(
            @Valid EMRPatientLaboratory emrPatientLaboratory
            ,Errors errors
            ,final RedirectAttributes redirect
            ,Model model
            ) {
        FileInputResponse response = new FileInputResponse();
        List<String> initialPreview = new ArrayList<>();
        List<FileInputInitialPreviewConfig> initialPreviewConfig = new ArrayList<>();
        List<String> labFileUrls = new ArrayList<>();
        long patientId = emrPatientLaboratory.getPatient().getId();
        MultipartFile[] files = emrPatientLaboratory.getLabFiles();
        if(files.length > 0 && files[0].getOriginalFilename().isEmpty()) {
            EMRPatientLaboratory emrImg = this.emrPatientLaboratoryRepository.findById(emrPatientLaboratory.getId())
                    .orElseGet(EMRPatientLaboratory::new);
            labFileUrls = emrImg.getLabFileUrls();
        } else {
            for (int i = 0; i < files.length; i++) {
                try {
                    String fileExt = files[i].getOriginalFilename().substring(files[i].getOriginalFilename().lastIndexOf("."));
                    String fileName = "patient_labfile_" + patientId + '_' + i + "_" + System.currentTimeMillis() + fileExt;
                    FileDTO fileDTO = this.storageService.uploadFile(files[i], fileName);
                    labFileUrls.add(fileDTO.getDownloadUri());
                    initialPreview.add(fileDTO.getDownloadUri());
                    FileInputInitialPreviewConfig config = new FileInputInitialPreviewConfig();
                    config.setKey(String.valueOf(i));
                    config.setFileType(fileDTO.getContentType());
                    config.setType("image");
                    config.setCaption(fileName);
                    config.setDownloadUrl(fileDTO.getDownloadUri());
                    initialPreviewConfig.add(config);
                } catch (Exception e) {
                    response.setError(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        emrPatientLaboratory.setLabFileUrls(labFileUrls);
        Patient patient = this.patientRepository.findById(patientId).orElseGet(Patient::new);
        emrPatientLaboratory.setPatient(patient);
        this.emrPatientLaboratoryRepository.save(emrPatientLaboratory);

        response.setInitialPreview(initialPreview);
        response.setInitialPreviewConfig(initialPreviewConfig);
        return new ResponseEntity<>(response, null, HttpStatus.OK);
    }
}
