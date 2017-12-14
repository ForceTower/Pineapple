package com.forcetower.uefs.sagres_sdk.parsers;

import android.util.Log;
import android.util.SparseArray;

import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresClass;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDay;
import com.forcetower.uefs.sagres_sdk.utility.SagresDayUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by João Paulo on 29/11/2017.
 */

public class SagresClassParser {
    private static SparseArray<String> iterationPerDay;
    private static HashMap<String, SagresClass> codePerLessons;
    public static boolean failed;

    public static HashMap<String, List<SagresClassDay>> getCompleteSchedule(String html) {
        iterationPerDay = new SparseArray<>();
        codePerLessons = new HashMap<>();
        failed = false;

        Document startPage = Jsoup.parse(html);
        Element schedule = startPage.selectFirst("table[class=\"meus-horarios\"]");
        Element subtitle = startPage.selectFirst("table[class=\"meus-horarios-legenda\"]");

        if (findSchedule(schedule)) findDetails(subtitle);
        return getSchedule(codePerLessons);
    }

    private static boolean findSchedule(Element schedule) {
        if (schedule == null) {
            failed = true;
            iterationPerDay.put(1, "SEG");
            SagresClass clazz = new SagresClass("Seu sagres está com algo minimizado?");
            clazz.setName("Horarios não encontrados no sagres - Atualizaremos em 1h");
            clazz.addClazz("N01");
            clazz.addStartEndTime("Erro ao pegar info", "Observe se seu horario está na tela inicial", "SEG", ":)");
            Log.e(SagresPortalSDK.SAGRES_SDK_TAG, "Minimized! Not found \"meu horarios\"!");
            codePerLessons.put("Seu sagres está com algo minimizado?", clazz);
            return false;
        }

        Element body = schedule.selectFirst("tbody");
        Elements trs = body.select("tr");

        for (int i = 0; i < trs.size(); i++) {
            Element tr = trs.get(i);
            if (i == 0) {
                //Header -> days of class
                Elements ths = tr.select("th");
                for (int j = 0; j < ths.size(); j++) {
                    Element th = ths.get(j);
                    if (!th.text().trim().isEmpty()) {
                        iterationPerDay.put(j, th.text().trim());
                    }
                }
            } else {
                Elements tds = tr.select("td");
                String start = "";
                String end = "";
                for (int j = 0; j < tds.size(); j++) {
                    Element td = tds.get(j);

                    String classTime = td.text().trim();
                    if (classTime.trim().isEmpty()) {
                        continue;
                    }

                    String[] parts = classTime.split(" ");
                    String one = parts[0].trim();
                    String two = parts[1].trim();

                    if (j == 0) {
                        start = one;
                        end = two;
                    } else {
                        String day = iterationPerDay.get(j);
                        SagresClass clazz = codePerLessons.get(one);

                        if (clazz == null) clazz = new SagresClass(one);

                        clazz.addClazz(two);
                        clazz.addStartEndTime(start, end, day, two);

                        codePerLessons.put(one, clazz);
                    }
                }
            }
        }
        return true;
    }

    private static void findDetails(Element subtitle) {
        Element body = subtitle.selectFirst("tbody");
        Elements trs = body.select("tr");

        String currentCode = "undef";
        for (int i = 0; i < trs.size(); i++) {
            Element tr = trs.get(i);
            Elements tds = tr.select("td");
            String value = tds.get(1).text();

            Element td = tds.get(0);
            if (td.html().contains("&nbsp;")) {
                String[] parts = value.split("::");
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
                }
            } else {
                int splitPos = value.indexOf("-");
                String code = value.substring(0, splitPos).trim();
                String name = value.substring(splitPos + 1).trim();

                currentCode = code;
                SagresClass lesson = codePerLessons.get(code);
                lesson.setName(name);
                codePerLessons.put(code, lesson);
            }
        }
    }

    private static HashMap<String, List<SagresClassDay>> getSchedule(HashMap<String, SagresClass> classes) {
        HashMap<String, List<SagresClassDay>> classPerDay = new HashMap<>();

        for (int i = 1; i <= 7; i++) {
            String dayOfWeek = SagresDayUtils.getDayOfWeek(i);
            List<SagresClassDay> dayOfClass = new ArrayList<>();

            for (SagresClass uClass : classes.values()) {
                List<SagresClassDay> days = uClass.getDays();

                for (SagresClassDay clazz : days) {
                    if (clazz.getDay().equalsIgnoreCase(dayOfWeek)) {
                        dayOfClass.add(clazz);
                    }
                }
            }

            Collections.sort(dayOfClass);
            classPerDay.put(dayOfWeek, dayOfClass);
        }

        return classPerDay;
    }
}
