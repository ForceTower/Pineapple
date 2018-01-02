package com.forcetower.uefs.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.forcetower.uefs.database.entities.ACalendarItem;
import com.forcetower.uefs.database.repository.CalendarRepository;

import java.util.List;

/**
 * Created by João Paulo on 02/01/2018.
 */

public class CalendarItemCollectionViewModel extends ViewModel {
    private CalendarRepository repository;

    public CalendarItemCollectionViewModel(CalendarRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<ACalendarItem>> getCalendar() {
        return repository.getCalendar();
    }

    public void addCalendarItem(ACalendarItem item) {
        new AddCalendarItemTask(repository).execute(item);
    }

    public void deleteCalendarItem(ACalendarItem item) {
        new DeleteCalendarItemTask(repository).execute(item);
    }

    private static class AddCalendarItemTask extends AsyncTask<ACalendarItem, Void, Void> {
        private final CalendarRepository repository;

        AddCalendarItemTask(CalendarRepository repository) {
            this.repository = repository;
        }

        @Override
        protected Void doInBackground(ACalendarItem... aCalendarItems) {
            repository.insertItems(aCalendarItems[0]);
            return null;
        }
    }

    private static class DeleteCalendarItemTask extends AsyncTask<ACalendarItem, Void, Void> {
        private final CalendarRepository repository;

        DeleteCalendarItemTask(CalendarRepository repository) {
            this.repository = repository;
        }

        @Override
        protected Void doInBackground(ACalendarItem... aCalendarItems) {
            repository.deleteItem(aCalendarItems[0]);
            return null;
        }
    }

}
