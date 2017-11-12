package com.forcetower.uefs.html_parser;

import android.util.Log;
import android.util.SparseArray;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.model.UClass;

import java.util.HashMap;

/**
 * Created by João Paulo on 10/11/2017.
 */

public class SagresParser {
    private static final String LOGIN_ERROR_PATTERN = "<div class=\"externo-erro\">";
    private static final String USER_NAME_PATTERN = "<span id=\"ctl00_ConteudoTopo_UserName\" class=\"usuario-nome\">";
    private static final String SCHEDULE_PATTERN = "<table class=\"meus-horarios\" cellspacing=\"0\" rules=\"all\" border=\"1\"";
    private static final String SCHEDULE_SUB_PATTERN = "<table class=\"meus-horarios-legenda\" cellspacing=\"0\" rules=\"all\" border=\"1\"";


    private static SparseArray<String> iterationPerDay = new SparseArray<>();
    private static HashMap<String, UClass> codePerLessons = new HashMap<>();

    public static String changeCharset(String html) {
        return ParserUtils.useReplacements(html);
    }

    public static boolean connected(String html) {
        if (html.contains(LOGIN_ERROR_PATTERN)) {
            int start = html.indexOf(LOGIN_ERROR_PATTERN) + LOGIN_ERROR_PATTERN.length();
            int end = html.indexOf("</div>", start);

            String loginError = html.substring(start, end).trim();
            if (loginError.length() != 0) {
                Log.i(Constants.APP_TAG, "[Probability] Login failed by user mistake");
                return false;
            } else {
                Log.i(Constants.APP_TAG, "[Probability] Login failed by my mistake");
                return false;
            }
        } else {
            Log.i(Constants.APP_TAG, "[Probability] Correct Login");
            return true;
        }
    }

    public static String getUserName(String html) {
        if (html.contains(USER_NAME_PATTERN)) {
            int start = html.indexOf(USER_NAME_PATTERN) + USER_NAME_PATTERN.length();
            int end = html.indexOf("</span>", start);

            String name = html.substring(start, end).trim();
            return Utils.toTitleCase(name);
        } else {
            return null;
        }
    }

    public static void findSchedule(String html) {
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

                        UClass clazz = codePerLessons.get(parts[0].trim());
                        if (clazz == null) {
                            clazz = new UClass(parts[0].trim());
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

    public static HashMap<String, UClass> findDetails(String html) {
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
                UClass lesson = codePerLessons.get(code);
                lesson.setName(name);
                codePerLessons.put(code, lesson);
            } else {
                String[] parts = td_2.split("::");

                if (parts.length == 2) {
                    if (!currentCode.equals("undef")) {
                        UClass lesson = codePerLessons.get(currentCode);
                        lesson.addAtToAllClasses(parts[1].trim());
                    }
                } else if (parts.length == 3) {
                    if (!currentCode.equals("undef")) {
                        UClass lesson = codePerLessons.get(currentCode);
                        lesson.addAtToSpecificClass(parts[2].trim(), parts[1].trim(), parts[0].trim());
                    }
                } else {
                    Log.i(Constants.APP_TAG, "Problem in parser. Size is not 2 nor 3");
                }
            }

            position = tr_end + 5;
        }

        return;
    }
}
