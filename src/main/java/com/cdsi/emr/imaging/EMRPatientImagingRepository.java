package com.cdsi.emr.imaging;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "imagings", path= "patientImaging")
public interface EMRPatientImagingRepository extends JpaRepository<EMRPatientImaging, Long> {

    List<EMRPatientImaging> findByPatientId(long patientId);
    List<EMRPatientImaging> findByPatientIdOrderByDateCreatedAsc(long patientId);

    @Query(nativeQuery = true,
            value= "select * from emrpatient_imaging where patient_id in ?1")
    List<EMRPatientImaging> findAllByDoctorId(List<Long> ids);

}
