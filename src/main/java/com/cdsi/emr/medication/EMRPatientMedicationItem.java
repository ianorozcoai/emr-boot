package com.cdsi.emr.medication;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
public class EMRPatientMedicationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = " is mandatory.")
    private String genericName = "genericName";

    private String brandName;
    
    private String dosage = "dosage";
    
    private String unitOfMeasure;

//    @NotBlank(message = " is mandatory.")
    private String remarks;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emrpatient_medication_id")
    @ToString.Exclude
    private EMRPatientMedication emrPatientMedication;
}
