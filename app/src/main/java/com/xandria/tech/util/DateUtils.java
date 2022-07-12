package com.xandria.tech.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateUtils {

    public static String getDateTimeString(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG)
                .withLocale(Locale.getDefault());
        LocalDate localDate = localDateTime.toLocalDate();
        LocalTime localTime = localDateTime.toLocalTime();

        return dateTimeFormatter.format(localDate)
                + " " + DateTimeFormatter.ofPattern("hh:mm a").format(localTime);
    }

    public static String getDateString(LocalDate localDate){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG)
                .withLocale(Locale.getDefault());
        return dateTimeFormatter.format(localDate);
    }

    public static String getTimeString(LocalTime localTime){
        return DateTimeFormatter.ofPattern("hh:mm a").format(localTime);
    }
}
