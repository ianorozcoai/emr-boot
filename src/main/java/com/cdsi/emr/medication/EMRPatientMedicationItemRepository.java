package com.cdsi.emr.medication;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EMRPatientMedicationItemRepository extends JpaRepository<EMRPatientMedicationItem, Long> {

    @Modifying
    @Query(value="DELETE FROM emrpatient_medication_item WHERE emrpatient_medication_id = :id", nativeQuery = true)
    void deleteByEMRPatientMedicationId(long id);

    @Query(nativeQuery = true,
            value = "select * from emrpatient_medication_item where emrpatient_medication_id in ?1")
    List<EMRPatientMedicationItem> findAllByDoctorId(List<Long> patientIds);

}
