package com.cdsi.emr.medicalrequest;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
public class EMRPatientMedicalRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = " is mandatory.")
    private String requestName = "requestName";    
    
    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emrpatient_medical_request_id")
    @ToString.Exclude
    private EMRPatientMedicalRequest emrPatientMedicalRequest;
}
