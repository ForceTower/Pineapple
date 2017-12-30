package com.forcetower.uefs.database.repository;

import com.forcetower.uefs.database.dao.ACalendarItemDao;
import com.forcetower.uefs.database.entities.ACalendarItem;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class CalendarRepository {
    private final ACalendarItemDao dao;

    public CalendarRepository(ACalendarItemDao dao) {
        this.dao = dao;
    }

    public List<ACalendarItem> getCalendar() {
        return dao.getCalendar();
    }

    public void insertItems(ACalendarItem... items) {
        dao.insertItems(items);
    }

    public void deleteItem(ACalendarItem item) {
        dao.deleteItem(item);
    }

    public void deleteCalendar() {
        dao.deleteCalendar();
    }
}
