package com.forcetower.uefs.sagres_sdk.parsers;

import android.util.Log;
import android.util.SparseArray;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.sagres_sdk.domain.SagresClass;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDay;
import com.forcetower.uefs.sagres_sdk.utility.SagresDayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by João Paulo on 18/11/2017.
 */

public class SagresScheduleParser {
    private static final String SCHEDULE_PATTERN = "<table class=\"meus-horarios\" cellspacing=\"0\" rules=\"all\" border=\"1\"";
    private static final String SCHEDULE_SUB_PATTERN = "<table class=\"meus-horarios-legenda\" cellspacing=\"0\" rules=\"all\" border=\"1\"";

    private static SparseArray<String> iterationPerDay;
    private static HashMap<String, SagresClass> codePerLessons;

    public static HashMap<String, List<SagresClassDay>> getCompleteSchedule(String html) {
        findSchedule(html);
        return getSchedule(findDetails(html));
    }

    public static HashMap<String, SagresClass> findSchedule(String html) {
        iterationPerDay = new SparseArray<>();
        codePerLessons = new HashMap<>();

        if (html.contains(SCHEDULE_PATTERN)) {
            int start = html.indexOf(SCHEDULE_PATTERN) + SCHEDULE_PATTERN.length();
            int end = html.indexOf("</table>", start);
            String schedule = html.substring(start, end);

            int header_start = schedule.indexOf("<tr>") + 4;
            int header_end = schedule.indexOf("</tr>", header_start);
            String section = schedule.substring(header_start, header_end);

            extractScheduleHeader(section.trim());

            String remaining = schedule.substring(header_end + 5);
            parseSchedule(remaining);
        }

        return codePerLessons;
    }

    private static void extractScheduleHeader(String section) {
        final String column_pattern_start = "<th scope=\"col\">";
        final String column_pattern_end = "</th>";

        int iteration = 0;
        int position = 0;

        while (position < section.length()) {
            int start = section.indexOf(column_pattern_start, position) + column_pattern_start.length();
            int end = section.indexOf(column_pattern_end, start);

            if (start - column_pattern_start.length() == -1) {
                return;
            }

            String day = section.substring(start, end);

            if (day.trim().length() > 1) {
                iterationPerDay.put(iteration, day);
            }

            iteration++;
            position = end;
        }
    }

    private static void parseSchedule(String schedule) {
        int position = 0;

        while (position < schedule.trim().length()) {
            int start = schedule.indexOf("<tr>", position);
            int end = schedule.indexOf("</tr>", start);

            if (start == -1) {
                return;
            }

            start += 4;
            String line = schedule.substring(start, end);
            line = line.trim();

            processLineOfClasses(line);

            position = end + 5;
        }
    }

    private static void processLineOfClasses(String line) {
        int position = 0;
        int iterations = 0;

        String classStart = "";
        String classFinish = "";

        while (position < line.length()) {
            int start = line.indexOf("<td", position);
            int end = line.indexOf("</td>", start);

            if (start == -1) {
                return;
            }

            start += 3;
            String process = line.substring(start, end);
            int real_start = process.indexOf(">") + 1;
            process = process.substring(real_start);


            if (process.trim().length() > 1) {
                String[] parts = process.split("<br />");
                if (parts.length == 2) {
                    if (iterations > 0) {
                        String key = iterationPerDay.get(iterations);

                        SagresClass clazz = codePerLessons.get(parts[0].trim());
                        if (clazz == null) {
                            clazz = new SagresClass(parts[0].trim());
                        }

                        clazz.addClazz(parts[1]);
                        clazz.addStartEndTime(classStart, classFinish, key, parts[1]);

                        codePerLessons.put(parts[0].trim(), clazz);
                    } else {
                        classStart = parts[0];
                        classFinish = parts[1];
                    }
                }
            }

            position = end + 5;
            iterations++;
        }
    }

    public static HashMap<String, SagresClass> findDetails(String html) {
        if (html.contains(SCHEDULE_SUB_PATTERN)) {
            int start = html.indexOf(SCHEDULE_SUB_PATTERN) + SCHEDULE_SUB_PATTERN.length();
            int end = html.indexOf("</table>", start);

            String schedule = html.substring(start, end);

            int first_tr = schedule.indexOf("<tr>");
            int last_tr = schedule.lastIndexOf("</tr>");

            String subtitle = schedule.substring(first_tr, last_tr + 5);

            extractSubtitle(subtitle);
        }
        return codePerLessons;
    }

    private static void extractSubtitle(String subtitle) {
        int position = 0;
        String currentCode = "undef";

        while (position < subtitle.length()) {
            int tr_start = subtitle.indexOf("<tr>", position);
            int tr_end = subtitle.indexOf("</tr>", tr_start);

            if (tr_start == -1) {
                return;
            }

            tr_start += 4;
            String tr_value = subtitle.substring(tr_start, tr_end);

            int td1_start = tr_value.indexOf("<td") + 3;
            int td1_end = tr_value.indexOf("</td>");

            int td2_start = tr_value.lastIndexOf("<td>") + 4;
            int td2_end = tr_value.lastIndexOf("</td>");

            String td_1 = tr_value.substring(td1_start, td1_end);
            String td_2 = tr_value.substring(td2_start, td2_end);

            boolean newClassTd = false;

            if (td_1.contains("style=\"") || td_1.contains("bgcolor=\"")) {
                newClassTd = true;
            }

            if (newClassTd) {
                String code = td_2.substring(0, td_2.indexOf("-")).trim();
                String name = td_2.substring(td_2.indexOf("-") + 1).trim();

                currentCode = code;
                SagresClass lesson = codePerLessons.get(code);
                lesson.setName(name);
                codePerLessons.put(code, lesson);
            } else {
                String[] parts = td_2.split("::");

                if (parts.length == 2) {
                    if (!currentCode.equals("undef")) {
                        SagresClass lesson = codePerLessons.get(currentCode);
                        lesson.addAtToAllClasses(parts[1].trim());
                    }
                } else if (parts.length == 3) {
                    if (!currentCode.equals("undef")) {
                        SagresClass lesson = codePerLessons.get(currentCode);
                        lesson.addAtToSpecificClass(parts[2].trim(), parts[1].trim(), parts[0].trim());
                    }
                } else {
                    Log.i(Constants.APP_TAG, "Problem in parser. Size is not 2 nor 3");
                }
            }

            position = tr_end + 5;
        }

    }

    public static HashMap<String, List<SagresClassDay>> getSchedule(HashMap<String, SagresClass> classes) {
        HashMap<String, List<SagresClassDay>> classPerDay = new HashMap<>();

        for (int i = 1; i <= 7; i++) {
            String dayOfWeek = SagresDayUtils.getDayOfWeek(i);
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
}
