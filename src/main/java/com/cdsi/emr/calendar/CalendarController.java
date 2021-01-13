package com.cdsi.emr.calendar;

import com.cdsi.emr.consultation.EmrConsultation;
import com.cdsi.emr.consultation.EmrConsultationRepository;
import com.cdsi.emr.personnel.Personnel;
import com.cdsi.emr.util.DateUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CalendarController {

    private static final String ON_QUEUE = "ON QUEUE";
    private static final String CANCELLED = "CANCELLED";
    private static final String PROCESSED = "PROCESSED";

    private final EmrConsultationRepository emrConsultationRepository;

    public CalendarController (
            final EmrConsultationRepository emrConsultationRepository
    ) {
        this.emrConsultationRepository = emrConsultationRepository;
    }

    @GetMapping("/emrCalendar")
    public String getCalendarPage(Model model, Authentication auth) {
        Personnel loggedUser = (Personnel) auth.getPrincipal();

        List<EmrConsultation> emrConsultations =
                emrConsultationRepository.findAllByPersonnelIdAndConsultationDateBetweenOrderByConsultationDateAsc(loggedUser.getId(),
                        DateUtil.getCurrentDateMinusMonths(6), DateUtil.getCurrentDatePlusMonths(6));

        model.addAttribute("eventCalendarDtoList", toCalendarDto(emrConsultations));

        return "emr/emr_calendar";
    }

    private List<CalendarDto> toCalendarDto(List<EmrConsultation> emrConsultations) {
        final List<CalendarDto> eventCalendarDtoList = new ArrayList<>();

        emrConsultations.forEach(
                item -> {
                    String color = null;
                    switch (item.getConsultationStatus()) {
                        case CANCELLED:
                            color = "Red"; break;
                        case ON_QUEUE:
                            color = "Blue"; break;
                        case PROCESSED:
                            color = "Green"; break;
                    }
                    eventCalendarDtoList.add(
                            new CalendarDto(
                                    String.valueOf(item.getId()),
                                    item.getPatient().getLastName()+", "+item.getPatient().getFirstName(),
                                    DateUtil.getDateInString(item.getConsultationDate()),
                                    null,
                                    color)
                    );
                }
        );

        return eventCalendarDtoList;
    }
}
