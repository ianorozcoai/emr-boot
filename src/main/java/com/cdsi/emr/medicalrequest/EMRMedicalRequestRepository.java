package com.cdsi.emr.medicalrequest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EMRMedicalRequestRepository extends JpaRepository<EMRMedicalRequest, Long> {
	
	List<EMRMedicalRequest> findAllByDoctorIdOrderByMedicalRequestName(long doctorId);
	EMRMedicalRequest findByMedicalRequestNameAndDoctorId(String medicalRequestName, long doctorId);
}
