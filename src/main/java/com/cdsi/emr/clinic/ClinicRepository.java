package com.cdsi.emr.clinic;

import org.springframework.stereotype.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    
	List<Clinic> findAllByDoctorId(long doctorId);
	List<Clinic> findAllByDoctorIdOrderByName(long doctorId);
	
}
