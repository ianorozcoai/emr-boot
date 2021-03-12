package com.cdsi.emr.labs;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "laboratories", path = "patientLab")
public interface EMRPatientLaboratoryRepository extends JpaRepository<EMRPatientLaboratory, Long> {
    List<EMRPatientLaboratory> findAllByPatientIdOrderByDateCreatedAsc(long patientId);
    List<EMRPatientLaboratory> findByPatientId(long patientId);

    @Query(nativeQuery = true,
            value = "select * from emrpatient_laboratory where patient_id in ?1")
    List<EMRPatientLaboratory> findAllByDoctorId(List<Long> ids);
}
