package com.cdsi.emr.patient;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PatientRepository extends CrudRepository<Patient, Long> {
	
	List<Patient> findAllByDoctorId(long doctorId);
	List<Patient> findAllByDoctorIdOrderByLastName(long doctorId);
	
	List<Patient> findByIdIn(List<Long> patientIds);
	
	List<Patient> findByFirstNameAndLastNameAndBirthdate(String firstName, String lastName, LocalDate birthDate);
}
