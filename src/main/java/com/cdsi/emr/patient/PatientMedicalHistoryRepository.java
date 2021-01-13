package com.cdsi.emr.patient;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientMedicalHistoryRepository extends JpaRepository<PatientMedicalHistory, Long> {
	
	List<PatientMedicalHistory> findByPatientId(long patientId);
	
}
