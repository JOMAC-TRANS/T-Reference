/*
 * Created on 01-24-2009 4:03 PM
 */
package com.hccs.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * A Utility for Dates.
 */
public class DateUtilities {

    /**
     * Shorthand for <code>setTimeField(date, 23, 59, 59)</code>.
     *
     * @param date input date
     * @return returns the date with max time.
     */
    public static java.util.Date setMaxTime(java.util.Date date) {
        return setTimeField(date, 23, 59, 59, 999);
    }

    /**
     * Shorthand for <code>setTimeField(date, 0, 0, 0)</code>.
     *
     * @param date input date
     * @return returns the date with min time.
     */
    public static java.util.Date setMinTime(java.util.Date date) {
        return setTimeField(date, 0, 0, 0, 0);
    }

    /**
     * Sets the date's time field to specified time value.
     *
     * @param date input date.
     * @param hourOfDay hour of the day (1-23)
     * @param min minutes
     * @param sec seconds
     * @param millis milliseconds
     * @return returns the date with specified time.
     */
    public static java.util.Date setTimeField(java.util.Date date, int hourOfDay, int min, int sec, int millis) {
        if (date == null) {
            return null;
        }

        DateTime dateTime = new DateTime(date.getTime());

        dateTime = dateTime.withTime(hourOfDay, min, sec, millis);
        return dateTime.toDate();
    }

    /**
     * Converts date to International Date Line West used for validation
     * purposes like checking if GMT date is equal to date in International Date
     * Line West. This preserves the local timezone information.
     *
     * @param date the date to convert to date line west
     * @return returns the date equivalent to a date in Internatinal Date Line
     * West.
     */
    public static Date toDateLineWest(Date date) {
        return convertToTimeZone(date, TimeZone.getTimeZone("Etc/GMT+12"));
    }

    /**
     * This is a shorthand for
     * <code>fixDateForTimeZone(date, dstZone, false)</code>
     *
     * @param date the date to fix.
     * @param dstZone database time zone.
     * @return returns fixed date.
     */
    @Deprecated
    public static Date fixDateForTimeZone(Date date, TimeZone dstZone) {
        return fixDateForTimeZone(date, dstZone, false);
    }

    /**
     * Used when we want to save/view the exact date value in database. Since
     * database may have different timezone than user time zone. It will
     * actually get the range(in hours) from client to server and adds/subtracts
     * it to the given date.
     *
     * @param date date the date to fix.
     * @param dstZone database time zone.
     * @param revert
     * @return returns fixed date.
     */
    @Deprecated
    public static Date fixDateForTimeZone(Date date, TimeZone dstZone, boolean revert) {
        if (date == null) {
            return null;
        }

        int srcHourOffset = TimeZone.getDefault().getRawOffset();
        int dstHourOffset = dstZone.getRawOffset();

        int newOffset = revert ? dstHourOffset - srcHourOffset
                : srcHourOffset - dstHourOffset;

        return new Date(date.getTime() + newOffset);
    }

    /**
     * Offset from GMT will be added to <code>Date</code> object to fix the date
     * when sending to a database with GMT timezone. Time fields will be first
     * set to 0 to conform with SQL DATE while preserving the local timezone.
     *
     * @param date the date that's not in GMT timezone.
     * @return a date that has appended offset.
     */
    public static Date convertForGMT(Date date) {
        if (date == null) {
            return null;
        }

        if (TimeZone.getTimeZone("UTC").hasSameRules(
                new DateTime(date.getTime()).getZone().toTimeZone())) {
            return date;
        }

        DateTimeZone timezone = DateTimeZone.getDefault();
        DateTime dateTime = new DateTime(date.getTime());
        dateTime = dateTime.withTime(0, 0, 0, 0);

        dateTime = dateTime.plusMillis(timezone.getOffset(dateTime.getMillis()));
        return dateTime.toDate();
    }

    /**
     * This function converts a given date to a date equivalent in given
     * java.util.TimeZone while preserving the local timezone.
     *
     * @param date date to convert
     * @param zone
     * @return date in given TimeZone.
     */
    public static Date convertToTimeZone(Date date, TimeZone zone) {
        if (date == null || zone == null) {
            return null;
        }

        DateTimeZone timezone = DateTimeZone.forTimeZone(zone);
        DateTime dateTime = new DateTime(date.getTime());

        dateTime = dateTime.toDateTime(timezone);
        dateTime = new DateTime(
                dateTime.getYear(),
                dateTime.getMonthOfYear(),
                dateTime.getDayOfMonth(),
                dateTime.getHourOfDay(),
                dateTime.getMinuteOfHour(),
                dateTime.getSecondOfMinute(),
                dateTime.getMillisOfSecond());

        return dateTime.toDate();
    }

    /**
     * This function checks if two dates are equal, null values are also
     * considered.
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean areDatesEqual(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return true;
        }
        return date1 != null && date2 != null && date1.equals(date2);
    }

    public enum FORMAT_TYPE {

        MM_DD_YYYY_DASH_WITH_TIMEZONE("MM-dd-yyyy"),
        MM_DD_YYYY_DASH("MM-dd-yyyy"),
        MM_DD_YYYY_HH_MM_A_DASH("MM-dd-yyyy hh:mm a"),
        MM_DD_YYYY_HH_MM_SS_A_DASH("MM-dd-yyyy hh:mm:ss a"),
        MM_DD_YYYY_FORWARD_SLASH("MM/dd/yyyy"),
        HH_MM("HH:mm"),
        HH_MM_WITH_TIMEZONE("HH:mm"),
        E_MM_DD_YYYY_HH_MM_SS_Z("E MM-dd-yyyy HH:mm:ss z"),
        TIMESTAMP("EEE, MMMM dd yyyy hh:mm:ss aaa"),
        YYYY_MM_DD("yyyy-MM-dd"),
        YYYY_MM_DD_HH_MM_A("yyyy-MM-dd hh:mm a");

        private final String value;

        private FORMAT_TYPE(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private static final Map<FORMAT_TYPE, SimpleDateFormat> formatters = new HashMap<>();

    /**
     * Gets a <code>SimpleDateFormat</code> from the map. If it does not exist
     * create it
     *
     * @param type the format of the <code>SimpleDateFormat</code> and serves as
     * the key on the map
     * @return the <code>SimpleDateFormat</code>
     */
    public static SimpleDateFormat getFormatter(FORMAT_TYPE type) {
        return getFormatter(type, false);
    }

    /**
     * Gets a <code>SimpleDateFormat</code> from the map. If it does not exist
     * create it
     *
     * @param type the format of the <code>SimpleDateFormat</code> and serves as
     * the key on the map
     * @param isToServerTZ
     * @return the <code>SimpleDateFormat</code>
     */
    public static SimpleDateFormat getFormatter(FORMAT_TYPE type, boolean isToServerTZ) {
        SimpleDateFormat sdf = new SimpleDateFormat(type.toString());
        if (isToServerTZ) {
            sdf.setTimeZone(TimeZone.getTimeZone("Universal"));
        }
        if (!formatters.containsKey(type)) {
            formatters.put(type, sdf);
        }
        return formatters.get(type);
    }

    /**
     * Gets the formatted string value of a date
     *
     * @param type the format of the <code>SimpleDateFormat</code>
     * @param date date to format
     * @return the formatted string value of a date
     */
    public static String format(FORMAT_TYPE type, Date date) {
        return getFormatter(type, false).format(date);
    }

    /**
     * Gets the parsed date from a string
     *
     * @param type the format of the <code>SimpleDateFormat</code>
     * @param date string to parse
     * @return the <code>Date</code> parsed from the string
     * @throws ParseException
     */
    public static Date parse(FORMAT_TYPE type, String date) throws ParseException {
        return getFormatter(type, false).parse(date);
    }

    /**
     * Gets the parsed date from a string
     *
     * @param type the format of the <code>SimpleDateFormat</code>
     * @param date string to parse
     * @return the <code>Date</code> parsed from the string
     * @throws ParseException
     */
    public static Date parseToUniversalTimeZone(FORMAT_TYPE type, String date) throws ParseException {
        return getFormatter(type, true).parse(date);
    }

    public static Date dateWithMaxDateInGMT(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        return cal.getTime();
    }

    public static String getMonthName(int index) {
        switch (index) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "";
        }
    }

    public static List<Integer> getMonths(Date fromDate, Date toDate) {
        List<Integer> monthRange = new ArrayList<>();

        LocalDate from = fromDate.toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate().withDayOfMonth(1);
        LocalDate to = toDate.toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate().withDayOfMonth(1);

        while (from.isBefore(to) || from.isEqual(to)) {
            monthRange.add(from.getMonthValue());
            from = from.plusMonths(1);
        }
        return monthRange;
    }
}
