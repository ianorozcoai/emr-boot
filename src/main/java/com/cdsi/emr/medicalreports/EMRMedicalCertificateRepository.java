package com.cdsi.emr.medicalreports;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EMRMedicalCertificateRepository extends JpaRepository<EMRMedicalCertificate, Long> {
	
	List<EMRMedicalCertificate> findAllByPatientId(long patientId);
	List<EMRMedicalCertificate> findByPatientIdOrderByDateRequestedDesc(long patientId);
	
}
