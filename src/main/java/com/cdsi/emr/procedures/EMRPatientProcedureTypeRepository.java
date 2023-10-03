package com.cdsi.emr.procedures;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EMRPatientProcedureTypeRepository extends JpaRepository<EMRPatientProcedureType, Long> {
	
	List<EMRPatientProcedureType> findAllByDoctorId(long doctorId);
	
}
