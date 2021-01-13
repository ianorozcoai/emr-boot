package com.cdsi.emr.consultation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmrConsultationRepository extends JpaRepository<EmrConsultation, Long> {

	List<EmrConsultation> findAllByConsultationDateAndCreatedBy(LocalDate consultationDate, String username);
	
	List<EmrConsultation> findAllByPersonnelIdAndConsultationDateBetweenOrderByConsultationDateAsc(long doctorId, LocalDate dateFrom, LocalDate dateTo);
	
	List<EmrConsultation> findAllByConsultationDateAndPersonnelId(LocalDate consultationDate, long doctorId);

	List<EmrConsultation> findAllByPatientId(long patientId);
	
	List<EmrConsultation> findAllByPersonnelId(long doctorId);
	
	List<EmrConsultation> findAllByPersonnelIdAndPaymentTypeAndHmoPaid(long doctorId, String paymentType, String hmoPaid);
	
	List<EmrConsultation> findAllByPersonnelIdAndPaymentType(long doctorId, String paymentType);

	List<EmrConsultation> findAllByIdInAndPaymentType(List<Long> consultationIds, String paymentType);

}
