package com.cdsi.emr.consultation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmrConsultationRepository extends JpaRepository<EmrConsultation, Long> {

	List<EmrConsultation> findAllByConsultationDateAndCreatedBy(LocalDate consultationDate, String username);
	
	List<EmrConsultation> findAllByPersonnelIdAndConsultationDateBetweenOrderByConsultationDateAsc(long doctorId, LocalDate dateFrom, LocalDate dateTo);
	
	List<EmrConsultation> findAllByConsultationDateAndPersonnelId(LocalDate consultationDate, long doctorId);
	
	List<EmrConsultation> findAllByConsultationDateAndPersonnelIdAndConsultationStatusOrderByConsultationDateDesc(LocalDate consultationDate, long doctorId, String status);

	List<EmrConsultation> findAllByPatientId(long patientId);
	
	List<EmrConsultation> findAllByPatientIdOrderByConsultationDateDesc(long patientId);
	
	List<EmrConsultation> findAllByPersonnelId(long doctorId);
	
	List<EmrConsultation> findAllByPersonnelIdOrderByConsultationDate(long doctorId);
	
	List<EmrConsultation> findAllByPersonnelIdAndPaymentTypeAndHmoPaid(long doctorId, String paymentType, String hmoPaid);
	
	List<EmrConsultation> findAllByPersonnelIdAndPaymentType(long doctorId, String paymentType);
	
	List<EmrConsultation> findAllByPersonnelIdAndPaymentTypeOrderByConsultationDate(long doctorId, String paymentType);

	List<EmrConsultation> findAllByIdInAndPaymentType(List<Long> consultationIds, String paymentType);
	
	// Add this to your EmrConsultationRepository interface
	Optional<EmrConsultation> findFirstByPatientIdAndPersonnelIdAndConsultationDateOrderByCreatedDateDesc(long patientId, long doctorId, LocalDate consultationDate);

	// Simplified method to verify connectivity
	List<EmrConsultation> findAllByPatientIdAndAndConsultationDate(long patientId, LocalDate consultationDate);
}
