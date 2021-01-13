package com.cdsi.emr.vaccination;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "vaccinations", path = "patientVaccination")
public interface EMRPatientVaccinationRepository extends JpaRepository<EMRPatientVaccination, Long> {

	List<EMRPatientVaccination> findByPatientId(long patientId);
	
}
