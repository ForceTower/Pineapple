package com.forcetower.uefs.sgrs.parsers;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.util.WordUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

import timber.log.Timber;

import static com.forcetower.uefs.util.ValueUtils.toDouble;

/**
 * Created by João Paulo on 06/03/2018.
 */

public class SagresGenericParser {

    public static String getName(Document document) {
        Element element = document.selectFirst("span[class=\"usuario-nome\"]");
        if (element != null) {
            return element.text().trim();
        } else {
            return null;
        }
    }

    public static double getScore(Document document) {
        List<Element> elements = document.select("div[class=\"situacao-escore\"]");
        for (Element element : elements) {
            if (element != null) {
                Element score = element.selectFirst("span[class=\"destaque\"]");
                if (score != null) {
                    try {
                        String text = score.text();
                        text = text.replaceAll("[^\\d,]", "");
                        text = text.replace(",", ".");
                        double d = toDouble(text, -1);
                        if (d != -1) return d;
                    } catch (Exception ignored){
                        Crashlytics.logException(ignored);
                    }
                } else {
                    Timber.d("Score element is null");
                    Crashlytics.log("Score element is null");
                }
            } else {
                Timber.d("Main Score element is null");
                Crashlytics.log("Main Score element is null");
            }
        }
        return -1;
    }
}
