package com.cdsi.emr.medicalrequest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EMRMedicalRequestGroupRepository extends JpaRepository<EMRMedicalRequestGroup, Long> {
	
	List<EMRMedicalRequestGroup> findAllByDoctorIdOrderByMedicalRequestGroupName(long doctorId);
	
}
