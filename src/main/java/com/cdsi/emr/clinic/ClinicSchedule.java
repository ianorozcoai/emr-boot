package com.cdsi.emr.clinic;

import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class ClinicSchedule {
    
    private String day;
    private String open;
    private String close;
}
