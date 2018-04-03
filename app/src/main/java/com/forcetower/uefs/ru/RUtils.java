package com.forcetower.uefs.ru;

import android.content.Context;
import android.support.annotation.NonNull;

import com.forcetower.uefs.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;

public class RUtils {
    public static final int COFFEE = 0;
    public static final int LUNCH  = 1;
    public static final int DINNER = 2;

    public static boolean isOpen(boolean open, int amount, int type) {
        if      (type == COFFEE) return open && amount != 320;
        else if (type == LUNCH)  return open && amount != 1450;
        else if (type == DINNER) return open && amount != 490;

        return false;
    }

    public static int getNextMealType(@NonNull Calendar calendar) {
        int hour    = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int account = (hour * 60) + minutes;

        if (account < 9.5 * 60) {
            return COFFEE;
        } else if (account < 14.5 * 60) {
            return LUNCH;
        } else if (account < 20 * 60) {
            return DINNER;
        }

        return COFFEE;
    }

    @NonNull
    public static String getNextMeal(@NonNull Context context, int type) {
        if      (type == COFFEE) return context.getResources().getString(R.string.ru_coffee);
        else if (type == LUNCH)  return context.getResources().getString(R.string.ru_lunch);
        else if (type == DINNER) return context.getResources().getString(R.string.ru_dinner);
        else                     return context.getResources().getString(R.string.ru_coffee);
    }

    @NonNull
    public static String getNextMealTime(@NonNull Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int type = getNextMealType(calendar);

        if (type == COFFEE) {
            if (day == SUNDAY) return "07h30min às 09h00min";
            return "06h30min às 09h00min";
        } else if (type == LUNCH) {
            if (day == SUNDAY) return "11h30min às 13h30min";
            if (day == SATURDAY) return "11h30min às 14h00min";
            return "10h30min às 14h00min";
        } else {
            if (day == SUNDAY) return "17h30min às 19h00min";
            if (day == SATURDAY) return "17h30min às 19h00min";
            return "17h30min às 19h30min";
        }
    }

    @NonNull
    public static String getPrice(int type, int amount) {
        switch (type) {
            case COFFEE:
                return (amount <= 0) ? "R$ 4,63" : "R$ 0,50";
            case LUNCH:
                return (amount <= 0) ? "R$ 8,56" : "R$ 1,00";
            case DINNER:
                return (amount <= 0) ? "R$ 3,94" : "R$ 0,70";
            default:
                return "R$ 1,00";
        }
    }

    @NonNull
    public static Calendar convertTime(@NonNull String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("pt", "br"));
            Date date = format.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date.getTime());
            calendar.add(Calendar.HOUR, -3);
            return calendar;
        } catch (ParseException e) {
            return Calendar.getInstance();
        }
    }
}
