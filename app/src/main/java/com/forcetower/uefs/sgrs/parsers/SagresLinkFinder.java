package com.forcetower.uefs.sgrs.parsers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by João Paulo on 23/03/2018.
 */

public class SagresLinkFinder {

    @Nullable
    public static String findForEnrollment(@NonNull Document document) {
        Element element = document.selectFirst("iframe");
        if (element == null) return null;
        return element.attr("src");
    }
}
