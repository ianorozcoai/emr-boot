package com.cdsi.emr.medicalrequest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EMRPatientMedicalRequestItemRepository extends JpaRepository<EMRPatientMedicalRequestItem, Long> {

    @Modifying
    @Query(value="DELETE FROM emrpatient_medical_request_item WHERE emrpatient_medical_request_id = :id", nativeQuery = true)
    void deleteByEMRPatientMedicalRequestId(long id);

    @Query(nativeQuery = true,
            value = "select * from emrpatient_medical_request_item where emrpatient_medical_request_id in ?1")
    List<EMRPatientMedicalRequestItem> findAllByDoctorId(List<Long> patientIds);

}
