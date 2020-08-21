package tech.demur.habittracker.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
    public static Date roundDateDay(Date inputDate) {
        Calendar clndr = new GregorianCalendar();
        clndr.setTime(inputDate);
        clndr.set(Calendar.HOUR_OF_DAY, 0);
        clndr.set(Calendar.MINUTE, 0);
        clndr.set(Calendar.SECOND, 0);
        clndr.set(Calendar.MILLISECOND, 0);
        return clndr.getTime();
    }

    public static Date roundDateWeek(Date inputDate) {
        Calendar clndr = new GregorianCalendar();
        clndr.setTime(inputDate);
        clndr.set(Calendar.HOUR_OF_DAY, 0);
        clndr.set(Calendar.MINUTE, 0);
        clndr.set(Calendar.SECOND, 0);
        clndr.set(Calendar.MILLISECOND, 0);
        clndr.add(Calendar.DAY_OF_MONTH, 1 - clndr.get(Calendar.DAY_OF_WEEK));
        return clndr.getTime();
    }

    public static Date roundDateMonth(Date inputDate) {
        Calendar clndr = new GregorianCalendar();
        clndr.setTime(inputDate);
        clndr.set(Calendar.DAY_OF_MONTH, 1);
        clndr.set(Calendar.HOUR_OF_DAY, 0);
        clndr.set(Calendar.MINUTE, 0);
        clndr.set(Calendar.SECOND, 0);
        clndr.set(Calendar.MILLISECOND, 0);
        return clndr.getTime();
    }
}