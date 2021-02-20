package com.cdsi.emr.personnel.staff;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.personnel.PersonnelDto;
import com.cdsi.emr.personnel.PersonnelRepository;

@Controller
public class StaffController {

    private PersonnelRepository staffRepository;

    public StaffController(PersonnelRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @GetMapping("/mystaffs")
    public String findAllStaffByDoctorId(Model model
            , @AuthenticationPrincipal Personnel personnel
            ) {
        List<Personnel> staffs = this.staffRepository.findAllByStaffSupervisorId(personnel.getId());
        model.addAttribute("personnels", staffs);
        model.addAttribute("personnel", new Personnel());
        model.addAttribute("personnelDto", new PersonnelDto());
        return "personnel/staff_list";
    }
}
