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
}
