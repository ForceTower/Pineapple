package com.forcetower.uefs.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.db.dao.CalendarItemDao;
import com.forcetower.uefs.db.entity.CalendarItem;
import com.forcetower.uefs.rep.sgrs.RefreshRepository;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.util.AbsentLiveData;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 08/03/2018.
 */
public class CalendarViewModel extends ViewModel {
    private final CalendarItemDao calendarItemDao;
    private final RefreshRepository repository;

    private LiveData<List<CalendarItem>> calendar;
    private boolean refreshing;
    private LiveData<Resource<Integer>> refresh;

    @Inject
    CalendarViewModel(CalendarItemDao calendarDao, RefreshRepository repository) {
        this.repository = repository;
        this.calendarItemDao = calendarDao;
    }

    public LiveData<List<CalendarItem>> getCalendar() {
        if (calendar == null)
            calendar = calendarItemDao.getCalendar();
        return calendar;
    }

    public LiveData<Resource<Integer>> refreshManual(boolean start) {
        if (start) {
            if (refresh == null) {
                refresh = repository.refreshData();
            }
            return refresh;
        } else {
            if (refresh == null)
                return AbsentLiveData.create();
            return refresh;
        }
    }

    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        if (!refreshing) refresh = null;
    }

    public boolean isRefreshing() {
        return refreshing;
    }
}
