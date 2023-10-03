package com.cdsi.emr.labs;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EMRPatientLaboratoryTypeRepository extends JpaRepository<EMRPatientLaboratoryType, Long> {

    List<EMRPatientLaboratoryType> findAllByDoctorId(long doctorId);
}
