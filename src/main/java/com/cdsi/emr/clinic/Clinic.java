package com.cdsi.emr.clinic;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
public class Clinic {	

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Positive(message = " is mandatory.")
    private long doctorId;
    
    @NotBlank(message = " is mandatory.")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = " is mandatory.")
    @Column(nullable = false)
    private String address;
    
    private String scheduleRx;
    
    private String contactNumber;
    
    @ElementCollection
    @ToString.Exclude
    List<ClinicSchedule> schedules = new ArrayList<>();
}
