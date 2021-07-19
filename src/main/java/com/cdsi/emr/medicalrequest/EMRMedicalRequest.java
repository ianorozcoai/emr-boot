package com.cdsi.emr.medicalrequest;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class EMRMedicalRequest {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = " is mandatory.")
    private String medicalRequestName;

    private long doctorId;
    
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emr_medical_request_group_id")
    EMRMedicalRequestGroup emrMedicalRequestGroup;
}
