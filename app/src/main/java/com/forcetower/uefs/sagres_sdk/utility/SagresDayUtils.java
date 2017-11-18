package com.forcetower.uefs.sagres_sdk.utility;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by João Paulo on 18/11/2017.
 */

public class SagresDayUtils {
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillis = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }

    public static Calendar generateCalendar(String str) {
        Calendar calendar = Calendar.getInstance();
        String[] parts = str.trim().split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(parts[1]));

        if (parts.length == 3)
            calendar.set(Calendar.SECOND, Integer.parseInt(parts[2]));

        return calendar;
    }

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

    public static int compareDayOfWeek(String one, String two) {
        int first = dayToInt(one);
        int second = dayToInt(two);

        if (first < second) {
            System.out.println(one + " > " + second);
            return -1;
        } else if (second > first) {
            System.out.println(one + " < " + second);
            return 1;
        } else {
            System.out.println(one + " = " + second);
            return 0;
        }
    }

    public static int dayToInt(String day) {
        if (day.equalsIgnoreCase("seg"))
            return 1;
        else if (day.equalsIgnoreCase("ter"))
            return 2;
        else if (day.equalsIgnoreCase("qua"))
            return 3;
        else if (day.equalsIgnoreCase("qui"))
            return 4;
        else if (day.equalsIgnoreCase("sex"))
            return 5;
        else if (day.equalsIgnoreCase("sab"))
            return 6;
        else if (day.equalsIgnoreCase("dom"))
            return 7;

        return 0;
    }
}
