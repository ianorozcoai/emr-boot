package com.cdsi.emr.dashboard;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdsi.emr.imaging.EMRPatientImaging;
import com.cdsi.emr.imaging.EMRPatientImagingRepository;
import com.cdsi.emr.labs.EMRPatientLaboratory;
import com.cdsi.emr.labs.EMRPatientLaboratoryRepository;
import com.cdsi.emr.medication.EMRPatientMedication;
import com.cdsi.emr.medication.EMRPatientMedicationRepository;
import com.cdsi.emr.others.EMRPatientOthers;
import com.cdsi.emr.others.EMRPatientOthersRepository;
import com.cdsi.emr.patient.Patient;
import com.cdsi.emr.patient.PatientRepository;
import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.procedures.EMRPatientProcedureRepository;
import com.cdsi.emr.vaccination.EMRPatientVaccinationRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class DataExportController {

    PatientRepository patientRepo;
    EMRPatientImagingRepository imagingRepo;
    EMRPatientLaboratoryRepository labsRepo;
    EMRPatientMedicationRepository medicationRepo;
    EMRPatientOthersRepository othersRepo;
    EMRPatientProcedureRepository procedureRepo;
    EMRPatientVaccinationRepository vaccinationRepo;

    @GetMapping("/exportdata/patients")
    List<Patient> allPatientsByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        return this.patientRepo.findAllByDoctorId(doctor.getId());
    }

    @GetMapping("/exportdata/imagings")
    List<EMRPatientImaging> allImagingByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        return this.imagingRepo.findAllByDoctorId(doctor.getId());
    }

    @GetMapping("/exportdata/labs")
    List<EMRPatientLaboratory> allLabsByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        return this.labsRepo.findAllByDoctorId(doctor.getId());
    }

    @GetMapping("/exportdata/medications")
    List<EMRPatientMedication> allMedicationsByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        return this.medicationRepo.findAllByDoctorId(doctor.getId());
    }

    @GetMapping("/exportdata/others")
    List<EMRPatientOthers> allOthersByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        return this.othersRepo.findAllByDoctorId(doctor.getId());
    }

    @GetMapping("/exportdata/procedures")
    List<EMRPatientOthers> allProceduresByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        return this.procedureRepo.findAllByDoctorId(doctor.getId());
    }

    @GetMapping("/exportdata/vaccinations")
    List<EMRPatientOthers> allVaccinationsByDoctorId(@AuthenticationPrincipal Personnel doctor) {
        return this.vaccinationRepo.findAllByDoctorId(doctor.getId());
    }
}
