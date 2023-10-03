package com.cdsi.emr.therapy;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "therapy", path = "patientTherapy")
public interface EMRPatientTherapyRepository extends JpaRepository<EMRPatientTherapy, Long> {

    List<EMRPatientTherapy> findByPatientId(long patientId);
    List<EMRPatientTherapy> findByPatientIdOrderByDateCreatedDesc(long patientId);

    @Query(nativeQuery = true,
            value = "select * from emrpatient_therapy where patient_id in ?1")
    List<EMRPatientTherapy> findAllByDoctorId(List<Long> ids);
    
    @Query(nativeQuery = true,
            value = "select * from emrpatient_therapy where patient_id in ?1 order by date_created")
    List<EMRPatientTherapy> findAllByPatientIdOrderByDateCreated(List<Long> ids);

}
