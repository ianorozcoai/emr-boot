package com.cdsi.emr.medication;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "medications", path = "patientMedication")
public interface EMRPatientMedicationRepository extends JpaRepository<EMRPatientMedication, Long> {

    List<EMRPatientMedication> findByPatientId(long patientId);

    @Query(nativeQuery = true,
            value = "select * from emrpatient_medication where patient_id in ?1")
    List<EMRPatientMedication> findAllByDoctorId(List<Long> ids);
}
