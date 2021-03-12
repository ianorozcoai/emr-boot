package com.cdsi.emr.others;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "others", path = "patientOthers")
public interface EMRPatientOthersRepository extends JpaRepository<EMRPatientOthers, Long> {

    List<EMRPatientOthers> findByPatientId(long patientId);

    @Query(nativeQuery = true,
            value = "")
    List<EMRPatientOthers> findAllByDoctorId(long id);

}
