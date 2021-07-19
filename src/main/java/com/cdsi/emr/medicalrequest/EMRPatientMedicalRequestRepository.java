package com.cdsi.emr.medicalrequest;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "medications", path = "patientMedication")
public interface EMRPatientMedicalRequestRepository extends JpaRepository<EMRPatientMedicalRequest, Long> {

    List<EMRPatientMedicalRequest> findByPatientId(long patientId);
    List<EMRPatientMedicalRequest> findByPatientIdOrderByDateCreatedDesc(long patientId);

    @Query(nativeQuery = true,
            value = "select * from emrpatient_medical_request where patient_id in ?1")
    List<EMRPatientMedicalRequest> findAllByDoctorId(List<Long> ids);
}
