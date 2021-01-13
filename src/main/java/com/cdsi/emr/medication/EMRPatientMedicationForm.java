package com.cdsi.emr.medication;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class EMRPatientMedicationForm {

    private EMRPatientMedication emrPatientMedication;
    
    private List<EMRPatientMedicationItem> emrPatientMedicationItems = new ArrayList<EMRPatientMedicationItem>();
}
