package com.forcetower.uefs.sgrs.parsers;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.db.entity.CalendarItem;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by João Paulo on 06/03/2018.
 */

public class SagresCalendarParser {

    public static List<CalendarItem> getCalendar(Document document) {
        Element element = document.selectFirst("div[class=\"webpart-calendario\"]");
        if (element == null) {
            Timber.d("Calendar not found");
            Crashlytics.log("Calendar not found");
            return null;
        }

        if (element.childNodeSize() < 2) {
            Timber.d("Calendar found, but not able to parse");
            Crashlytics.log("Calendar found, but not able to parse");
            return null;
        }

        List<CalendarItem> items = new ArrayList<>();
        Element events = element.child(1);
        Element ul = events.selectFirst("ul");

        for (Element li : ul.select("li")) {
            String text = li.text();
            int index = text.indexOf("-");
            String days = text.substring(0, index);
            String event = text.substring(index + 1);
            items.add(new CalendarItem(days, event.trim()));
        }

        return items;
    }
}
