package com.forcetower.uefs.sgrs.parsers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.crashlytics.android.Crashlytics;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by João Paulo on 23/03/2018.
 */

public class SagresLinkFinder {

    @Nullable
    public static String findForDocument(@NonNull Document document) {
        Element element = document.selectFirst("iframe");
        if (element == null) {
            Crashlytics.log("Document link element iframe was not found... Someone's dog will get sad");
            return null;
        }

        if (element.attr("src") == null) {
            Crashlytics.log("No source in iframe. That's cool");
        }
        return element.attr("src");
    }
}
