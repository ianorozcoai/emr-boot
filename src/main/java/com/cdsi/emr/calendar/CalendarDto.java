package com.cdsi.emr.calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDto implements Comparable<CalendarDto> {
    private String id;
    private String title;
    private String start;
    private String end;
    private String color;
    private String url;

    @Override
    public int compareTo(CalendarDto o) {
        if (this.title != null && o != null && o.getTitle() != null) {
            return this.title.compareTo(o.getTitle());
        } else {
            return -1;
        }
    }
}
