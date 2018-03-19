package com.forcetower.uefs.sgrs.parsers;

import com.forcetower.uefs.util.WordUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
        Element element = document.selectFirst("div[class=\"situacao-escore\"]");
        if (element != null) {
            Element score = element.selectFirst("span[class=\"destaque\"]");
            if (score != null) {
                String text = score.text();
                if (text.endsWith("*")) text = text.substring(0, text.length() - 1);
                text = text.replace(",", ".");
                return toDouble(text, -1);
            }
        }
        return -1;
    }
}
