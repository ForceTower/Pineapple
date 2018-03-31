package com.forcetower.uefs.util;

import android.content.Context;

import com.forcetower.uefs.R;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by João Paulo on 17/01/2018.
 */

public class DateUtils {
    /*
    public static String getMonthString(Context mContext, String value) {
        int month = Integer.parseInt(value);
        if (month == 1)  return mContext.getString(R.string.january_ab);
        if (month == 2)  return mContext.getString(R.string.february_ab);
        if (month == 3)  return mContext.getString(R.string.march_ab);
        if (month == 4)  return mContext.getString(R.string.april_ab);
        if (month == 5)  return mContext.getString(R.string.may_ab);
        if (month == 6)  return mContext.getString(R.string.june_ab);
        if (month == 7)  return mContext.getString(R.string.july_ab);
        if (month == 8)  return mContext.getString(R.string.august_ab);
        if (month == 9)  return mContext.getString(R.string.september_ab);
        if (month == 10) return mContext.getString(R.string.october_ab);
        if (month == 11) return mContext.getString(R.string.november_ab);
        if (month == 12) return mContext.getString(R.string.december_ab);

        return mContext.getString(R.string.undefined);
    }
    */

    public static String getDayOfWeek(int i) {
        if (i == 1)
            return "SEG";
        else if (i == 2)
            return "TER";
        else if (i == 3)
            return "QUA";
        else if (i == 4)
            return "QUI";
        else if (i == 5)
            return "SEX";
        else if (i == 6)
            return "SAB";
        else if (i == 7)
            return "DOM";

        return "???";
    }

    public static String toWeekLongDay(Context context, String day) {
        if (day.equalsIgnoreCase("seg"))
            return context.getString(R.string.monday);
        else if (day.equalsIgnoreCase("ter"))
            return context.getString(R.string.tuesday);
        else if (day.equalsIgnoreCase("qua"))
            return context.getString(R.string.wednesday);
        else if (day.equalsIgnoreCase("qui"))
            return context.getString(R.string.thursday);
        else if (day.equalsIgnoreCase("sex"))
            return context.getString(R.string.friday);
        else if (day.equalsIgnoreCase("sab"))
            return context.getString(R.string.saturday);
        else if (day.equalsIgnoreCase("dom"))
            return context.getString(R.string.sunday);

        return day;
    }

    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    public static String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static String formatTimeWithMarker(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static int getHourOfDay(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("H", Locale.getDefault());
        return Integer.valueOf(dateFormat.format(timeInMillis));
    }

    public static int getMinute(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("m", Locale.getDefault());
        return Integer.valueOf(dateFormat.format(timeInMillis));
    }

    /**
     * If the given time is of a different date, display the date.
     * If it is of the same date, display the time.
     * @param timeInMillis  The time to convert, in milliseconds.
     * @return  The time or date.
     */
    public static String formatDateTime(long timeInMillis) {
        if(isToday(timeInMillis)) {
            return formatTime(timeInMillis);
        } else {
            return formatDate(timeInMillis);
        }
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    public static String formatDate(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }

    /**
     * Returns whether the given date is today, based on the user's current locale.
     */
    public static boolean isToday(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String date = dateFormat.format(timeInMillis);
        return date.equals(dateFormat.format(System.currentTimeMillis()));
    }

    /**
     * Checks if two dates are of the same day.
     * @param millisFirst   The time in milliseconds of the first date.
     * @param millisSecond  The time in milliseconds of the second date.
     * @return  Whether {@param millisFirst} and {@param millisSecond} are off the same day.
     */
    public static boolean hasSameDate(long millisFirst, long millisSecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(millisFirst).equals(dateFormat.format(millisSecond));
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillis = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }

    public static Calendar generateCalendar(String str) {
        Calendar calendar = Calendar.getInstance();
        String[] parts = str.trim().split(":");
        if (parts.length != 1) {
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(parts[1]));

            if (parts.length == 3)
                calendar.set(Calendar.SECOND, Integer.parseInt(parts[2]));
        }

        return calendar;
    }
}
