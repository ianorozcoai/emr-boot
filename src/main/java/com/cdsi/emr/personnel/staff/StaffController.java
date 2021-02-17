package com.cdsi.emr.personnel.staff;

import org.springframework.stereotype.Controller;

import com.cdsi.emr.personnel.PersonnelRepository;

@Controller
public class StaffController {

    private PersonnelRepository staffRepository;

    public StaffController(PersonnelRepository staffRepository) {
        this.staffRepository = staffRepository;
    }
}
