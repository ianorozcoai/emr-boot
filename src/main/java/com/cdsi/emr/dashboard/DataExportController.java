package com.cdsi.emr.dashboard;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdsi.emr.consultation.EmrConsultation;
import com.cdsi.emr.consultation.EmrConsultationRepository;
import com.cdsi.emr.imaging.EMRPatientImaging;
import com.cdsi.emr.imaging.EMRPatientImagingRepository;
import com.cdsi.emr.labs.EMRPatientLaboratory;
import com.cdsi.emr.labs.EMRPatientLaboratoryRepository;
import com.cdsi.emr.medication.EMRPatientMedication;
import com.cdsi.emr.medication.EMRPatientMedicationItem;
import com.cdsi.emr.medication.EMRPatientMedicationItemRepository;
import com.cdsi.emr.medication.EMRPatientMedicationRepository;
import com.cdsi.emr.others.EMRPatientOthers;
import com.cdsi.emr.others.EMRPatientOthersRepository;
import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;
import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.procedures.EMRPatientProcedure;
import com.cdsi.emr.procedures.EMRPatientProcedureRepository;
import com.cdsi.emr.vaccination.EMRPatientVaccination;
import com.cdsi.emr.vaccination.EMRPatientVaccinationRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class DataExportController {

    PatientRepository patientRepo;
    EmrConsultationRepository consultationRepo;
    EMRPatientImagingRepository imagingRepo;
    EMRPatientLaboratoryRepository labsRepo;
    EMRPatientMedicationRepository medicationRepo;
    EMRPatientMedicationItemRepository medicationItemRepo;
    EMRPatientOthersRepository othersRepo;
    EMRPatientProcedureRepository procedureRepo;
    EMRPatientVaccinationRepository vaccinationRepo;

    @GetMapping("/exportdata/patients")
    @Cacheable
    List<Patient> allPatientsByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        return this.patientRepo.findAllByDoctorIdOrderByLastName(doctor.getId());
    }

    @GetMapping("/exportdata/consultations")
    List<EmrConsultation> allConsultationsByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        return this.consultationRepo.findAllByPersonnelId(doctor.getId());
    }

    @GetMapping("/exportdata/imagings")
    List<EMRPatientImaging> allImagingByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        List<Patient> patients = this.patientRepo.findAllByDoctorId(doctor.getId());
        List<Long> patientIds = patients.stream()
                .map(Patient::getId)
                .collect(toList());
        return this.imagingRepo.findAllByDoctorId(patientIds);
    }

    @GetMapping("/exportdata/labs")
    List<EMRPatientLaboratory> allLabsByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        List<Patient> patients = this.patientRepo.findAllByDoctorId(doctor.getId());
        List<Long> patientIds = patients.stream()
                .map(Patient::getId)
                .collect(toList());
        return this.labsRepo.findAllByDoctorId(patientIds);
    }

    @GetMapping("/exportdata/medications")
    List<EMRPatientMedicationItem> allMedicationsByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        List<Patient> patients = this.patientRepo.findAllByDoctorId(doctor.getId());
        List<Long> patientIds = patients.stream()
                .map(Patient::getId)
                .collect(toList());
        List<Long> medicationIds = this.medicationRepo.findAllByDoctorId(patientIds).stream()
                .map(EMRPatientMedication::getId)
                .collect(toList());
        List<EMRPatientMedicationItem> result = this.medicationItemRepo.findAllByDoctorId(medicationIds);
        return result;
    }

    @GetMapping("/exportdata/others")
    List<EMRPatientOthers> allOthersByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        List<Patient> patients = this.patientRepo.findAllByDoctorId(doctor.getId());
        List<Long> patientIds = patients.stream()
                .map(Patient::getId)
                .collect(toList());
        return this.othersRepo.findAllByDoctorId(patientIds);
    }

    @GetMapping("/exportdata/procedures")
    List<EMRPatientProcedure> allProceduresByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        List<Patient> patients = this.patientRepo.findAllByDoctorId(doctor.getId());
        List<Long> patientIds = patients.stream()
                .map(Patient::getId)
                .collect(toList());
        return this.procedureRepo.findAllByDoctorId(patientIds);
    }

    @GetMapping("/exportdata/vaccinations")
    List<EMRPatientVaccination> allVaccinationsByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        List<Patient> patients = this.patientRepo.findAllByDoctorId(doctor.getId());
        List<Long> patientIds = patients.stream()
                .map(Patient::getId)
                .collect(toList());
        return this.vaccinationRepo.findAllByDoctorId(patientIds);
    }
}
