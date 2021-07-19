package com.cdsi.emr.medicalrequest;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import javax.validation.Valid;

@Data
public class EMRPatientMedicalRequestForm {

    @Valid
    private EMRPatientMedicalRequest emrPatientMedicalRequest;

    @Valid
    private List<EMRPatientMedicalRequestItem> emrPatientMedicalRequestItems = new ArrayList<EMRPatientMedicalRequestItem>();
}
