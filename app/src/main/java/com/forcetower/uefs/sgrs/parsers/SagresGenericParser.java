package com.forcetower.uefs.sgrs.parsers;

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
            return WordUtils.toTitleCase(element.text());
        } else {
            return null;
        }
    }

    public static double getScore(Document document) {
        List<Element> elements = document.select("div[class=\"situacao-escore\"]");
        for (Element element : elements) {
            //Element element = document.selectFirst("div[class=\"situacao-escore\"]");
            if (element != null) {
                Element score = element.selectFirst("span[class=\"destaque\"]");
                if (score != null) {
                    try {
                        String text = score.text();
                        text = text.replaceAll("[^\\d,]", "");
                        //if (text.endsWith("*")) text = text.substring(0, text.length() - 1);
                        text = text.replace(",", ".");
                        double d = toDouble(text, -1);
                        if (d != -1) return d;
                    } catch (Exception ignored){}
                } else {
                    Timber.d("Score element is null");
                }
            } else {
                Timber.d("Main Score element is null");
            }
        }
        return -1;
    }
}
