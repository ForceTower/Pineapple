package com.forcetower.uefs.sagres_sdk.parsers;

import android.util.Log;

import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresCalendarItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 02/12/2017.
 */
public class SagresCalendarParser {

    public static List<SagresCalendarItem> getCalendar(String html) {
        List<SagresCalendarItem> items = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Element element = document.selectFirst("div[class=\"webpart-calendario\"]");
        if (element.childNodeSize() < 2) {
            Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Incorrect amount of children");
            return items;
        }


        Element events = element.child(1);
        Element ul = events.selectFirst("ul");

        for (Element li : ul.select("li")) {
            String text = li.text();
            int index = text.indexOf("-");
            String days = text.substring(0, index);
            String event = text.substring(index + 1);
            items.add(new SagresCalendarItem(days, null, event.trim()));
            /*
            if (days.contains("a")) {
                String[] parts = days.split("a");
                //items.add(new SagresCalendarItem(parts[0].trim(), parts[1].trim(), event));
            } else {
                items.add(new SagresCalendarItem(days, null, event));
            }*/
        }

        return items;
    }
}
