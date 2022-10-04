package project.adam.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateUtils {

    public static String getFormattedDateTime(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();
        return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
    }

    public static String getFormattedDate(LocalDate dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        return year + "-" + month + "-" + day;
    }
}
