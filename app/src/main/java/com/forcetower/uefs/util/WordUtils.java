package com.forcetower.uefs.util;

import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static com.forcetower.uefs.Constants.URL_PATTERN;

/**
 * Created by João Paulo on 06/03/2018.
 */

public class WordUtils {
    public static String toTitleCase(String givenString) {
        if (givenString == null)
            return null;

        String[] arr = givenString.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String anArr : arr) {
            if (anArr.length() < 4 && !anArr.endsWith(".")) {
                sb.append(anArr).append(" ");
                continue;
            }

            sb.append(Character.toUpperCase(anArr.charAt(0)))
                    .append(anArr.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public static boolean validString(String string) {
        return string != null && !string.trim().isEmpty();
    }

    public static List<String> getLinksOnText(String string) {
        Matcher matcher = Patterns.WEB_URL.matcher(string);
        List<String> links = new ArrayList<>();

        if (!validString(string)) return links;

        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            links.add(string.substring(matchStart, matchEnd));
        }
        return links;
    }

    public static String buildFromStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Stack Trace START\n");
        for (StackTraceElement trace : stackTrace) {
            stringBuilder.append("\tat ").append(trace).append("\n");
        }
        stringBuilder.append("Stack Trace END");
        return stringBuilder.toString();
    }
}
