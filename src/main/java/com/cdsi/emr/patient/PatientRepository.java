package com.cdsi.emr.patient;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PatientRepository extends CrudRepository<Patient, Long> {
	
	List<Patient> findAllByDoctorId(long doctorId);
	
	@Query("SELECT p FROM Patient p WHERE p.doctor.id = :doctorId " +
	           "ORDER BY TRIM(p.lastName) ASC, TRIM(p.firstName) ASC")
	List<Patient> findAllByDoctorIdOrderByLastName(@Param("doctorId") long doctorId);
	
	List<Patient> findByIdIn(List<Long> patientIds);
	
	List<Patient> findByFirstNameAndLastNameAndBirthdate(String firstName, String lastName, LocalDate birthDate);
}