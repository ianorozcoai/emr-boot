package com.cdsi.emr.clinic;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    
}
