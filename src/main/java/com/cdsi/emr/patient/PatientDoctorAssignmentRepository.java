package com.cdsi.emr.patient;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientDoctorAssignmentRepository extends CrudRepository<PatientDoctorAssignment, Long> {

    List<PatientDoctorAssignment> findByAdmissionId(long admissionId);

    Optional<PatientDoctorAssignment> findByPatientIdAndAdmissionId(long patientId, long admissionId);

}
