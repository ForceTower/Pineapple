package com.forcetower.uefs.sagres_sdk.parsers;

import android.util.Log;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by João Paulo on 10/11/2017.
 */

public class SagresParser {
    private static final String LOGIN_ERROR_PATTERN = "<div class=\"externo-erro\">";

    public static String changeCharset(String html) {
        return SagresParserReplacements.useReplacements(html);
    }

    public static boolean connected(String html) {
        if (html.contains(LOGIN_ERROR_PATTERN)) {
            int start = html.indexOf(LOGIN_ERROR_PATTERN) + LOGIN_ERROR_PATTERN.length();
            int end = html.indexOf("</div>", start);

            String loginError = html.substring(start, end).trim();
            if (loginError.length() != 0) {
                Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "[Probability] Login failed by user mistake");
                return false;
            } else {
                Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "[Probability] Login failed by my mistake");
                return false;
            }
        } else {
            Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "[Probability] Correct Login");
            return true;
        }
    }

    public static String getUserName(String html) {
        Document document = Jsoup.parse(html);
        Element element = document.selectFirst("span[class\"usuario-nome\"]");
        if (element != null) {
            return Utils.toTitleCase(element.text());
        } else {
            return "Não foi possivel pegar o nome...";
        }
    }

    public static String getScore(String html) {
        Document document = Jsoup.parse(html);
        Element element = document.selectFirst("div[class=\"situacao-escore\"]");
        return element.selectFirst("span[class=\"destaque\"]").text();
    }
}
