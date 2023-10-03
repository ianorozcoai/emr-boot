package com.cdsi.emr.medication;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import javax.validation.Valid;

@Data
public class EMRPatientMedicationForm {

    @Valid
    private EMRPatientMedication emrPatientMedication;

    @Valid
    private List<EMRPatientMedicationItem> emrPatientMedicationItems = new ArrayList<EMRPatientMedicationItem>();
}
