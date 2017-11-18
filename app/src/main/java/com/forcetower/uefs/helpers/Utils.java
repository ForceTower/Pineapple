package com.forcetower.uefs.helpers;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.forcetower.uefs.R;
import com.forcetower.uefs.sagres_sdk.domain.SagresClass;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by João Paulo on 10/11/2017.
 */

public class Utils {
    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (String anArr : arr) {
            sb.append(Character.toUpperCase(anArr.charAt(0)))
                    .append(anArr.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

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

    public static boolean supportsMaterialDesign() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static void fadeIn(View v, Context context) {
        if (v.getVisibility() == View.VISIBLE) return;
        Animation fadeInAnim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        v.setVisibility(View.VISIBLE);
        v.startAnimation(fadeInAnim);
        v.requestLayout();
    }

    public static void fadeOut(View v, Context context) {
        if (v.getVisibility() == View.INVISIBLE) return;
        Animation fadeOutAnim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        v.startAnimation(fadeOutAnim);
        v.setVisibility(View.INVISIBLE);
        v.requestLayout();
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

    public static HashMap<String, List<SagresClassDay>> getSchedule(HashMap<String, SagresClass> classes) {
        HashMap<String, List<SagresClassDay>> classPerDay = new HashMap<>();

        for (int i = 1; i <= 7; i++) {
            String dayOfWeek = getDayOfWeek(i);
            List<SagresClassDay> dayOfClass = new ArrayList<>();

            for (SagresClass uclass : classes.values()) {
                List<SagresClassDay> days = uclass.getDays();

                for (SagresClassDay classz : days) {
                    if (classz.getDay().equalsIgnoreCase(dayOfWeek)) {
                        dayOfClass.add(classz);
                    }
                }
            }

            Collections.sort(dayOfClass);
            classPerDay.put(dayOfWeek, dayOfClass);
        }

        return classPerDay;
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
