package com.cdsi.emr.medication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EMRPatientMedicationItemRepository extends JpaRepository<EMRPatientMedicationItem, Long> {

    @Modifying
    @Query(value="DELETE FROM emrpatient_medication_item WHERE emrpatient_medication_id = :id", nativeQuery = true)
    void deleteByEMRPatientMedicationId(long id);
	
}
