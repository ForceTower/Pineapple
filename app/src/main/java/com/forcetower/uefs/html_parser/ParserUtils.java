package com.forcetower.uefs.html_parser;

import android.util.Pair;

/**
 * Created by João Paulo on 10/11/2017.
 */

public class ParserUtils {

    public static String useReplacements(String str) {
        String replaced = str;
        for (Pair pair : ParserReplacements.replacements) {
            replaced = replaced.replaceAll((String)pair.first, (String)pair.second);
        }
        return replaced;
    }
}
