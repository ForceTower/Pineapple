package com.forcetower.uefs.sgrs.parsers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by João Paulo on 23/03/2018.
 */

public class SagresLinkFinder {

    @Nullable
    public static String findForDocument(@NonNull Document document) {
        Element element = document.selectFirst("iframe");
        if (element == null) return null;
        return element.attr("src");
    }
}
