package com.cdsi.emr.imaging;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EMRPatientImagingTypeRepository extends JpaRepository<EMRPatientImagingType, Long> {

    List<EMRPatientImagingType> findAllByDoctorId(long doctorId);
}
