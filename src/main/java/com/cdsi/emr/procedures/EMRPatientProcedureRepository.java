package com.cdsi.emr.procedures;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "procedures", path = "patientProcedure")
public interface EMRPatientProcedureRepository extends JpaRepository<EMRPatientProcedure, Long> {

	List<EMRPatientProcedure> findByPatientId(long patientId);
	
}
