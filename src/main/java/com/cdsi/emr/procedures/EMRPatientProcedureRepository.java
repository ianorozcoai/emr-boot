package com.cdsi.emr.procedures;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.cdsi.emr.others.EMRPatientOthers;

@RepositoryRestResource(collectionResourceRel = "procedures", path = "patientProcedure")
public interface EMRPatientProcedureRepository extends JpaRepository<EMRPatientProcedure, Long> {

    List<EMRPatientProcedure> findByPatientId(long patientId);

    @Query(nativeQuery = true,
            value = "")
    List<EMRPatientOthers> findAllByDoctorId(long id);

}
