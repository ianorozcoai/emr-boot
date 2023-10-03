package com.cdsi.emr.therapy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EMRPatientTherapyTypeRepository extends JpaRepository<EMRPatientTherapyType, Long> {
	
	List<EMRPatientTherapyType> findAllByDoctorId(long doctorId);
	
}
