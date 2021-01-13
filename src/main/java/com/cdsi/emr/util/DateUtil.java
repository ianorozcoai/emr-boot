package com.cdsi.emr.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static String getDateInString(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return localDate.format(formatter);
    }

    public static LocalDate getCurrentDateMinusMonths(int monthsToMinus) {
        return LocalDate.now().minusMonths(monthsToMinus);
    }

    public static LocalDate getCurrentDatePlusMonths(int monthsToAdd) {
        return LocalDate.now().plusMonths(monthsToAdd);
    }
}
