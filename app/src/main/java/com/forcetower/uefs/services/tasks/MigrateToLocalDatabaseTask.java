package com.forcetower.uefs.services.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.database.AppDatabase;
import com.forcetower.uefs.database.entities.AAccess;
import com.forcetower.uefs.database.entities.ACalendarItem;
import com.forcetower.uefs.database.entities.ADiscipline;
import com.forcetower.uefs.database.entities.AScrap;
import com.forcetower.uefs.database.repository.AccessRepository;
import com.forcetower.uefs.database.repository.CalendarRepository;
import com.forcetower.uefs.database.repository.DisciplineRepository;
import com.forcetower.uefs.database.repository.ScrapRepository;
import com.forcetower.uefs.dependency_injection.component.ApplicationComponent;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.domain.SagresCalendarItem;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDetails;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class MigrateToLocalDatabaseTask extends AsyncTask<Void, Void, Void> {
    @Inject
    AccessRepository accessRepository;
    @Inject
    DisciplineRepository disciplineRepository;
    @Inject
    ScrapRepository scrapRepository;
    @Inject
    CalendarRepository calendarRepository;

    public MigrateToLocalDatabaseTask(ApplicationComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            scrapRepository.deleteAllScraps();
            calendarRepository.deleteCalendar();
            accessRepository.deleteAllAccesses();
            disciplineRepository.deleteAllDisciplines();

            /*
            SagresAccess access = SagresAccess.getCurrentAccess();
            database.aAccessDao().insertAccess(new AAccess(access.getUsername(), access.getPassword()));

            SagresProfile profile = SagresProfile.getCurrentProfile();

            List<SagresCalendarItem> calendar = profile.getCalendar();
            if (calendar != null && calendar.size() > 0) {
                ACalendarItem[] items = new ACalendarItem[calendar.size()];
                transformCalendar(calendar).toArray(items);
                database.aCalendarItemDao().insertItems(items);
                Log.d(APP_TAG, "Inserted calendar: " + Arrays.toString(items));
            }
            Log.d(APP_TAG, "All calendar: " + database.aCalendarItemDao().getCalendar());

            List<SagresMessage> messages = profile.getMessages();
            if (messages != null && messages.size() > 0) {
                AScrap[] items = new AScrap[messages.size()];
                transformScraps(messages).toArray(items);
                database.aScrapDao().insertScraps(items);
                Log.d(APP_TAG, "Inserted messages: " + Arrays.toString(items));
            }
            Log.d(APP_TAG, "All messages: " + database.aScrapDao().getAllScraps());

            handleDisciplines();

            */
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void handleDisciplines() {
        SagresProfile profile = SagresProfile.getCurrentProfile();


    }

    private List<AScrap> transformScraps(List<SagresMessage> before) {
        List<AScrap> after = new ArrayList<>();
        if (before == null) {
            return after;
        }
        for (SagresMessage old : before) {
            after.add(new AScrap(old.getSender(), old.getMessage(), old.getReceivedTime(), old.getClassName()));
        }

        return after;
    }


    private List<ACalendarItem> transformCalendar(List<SagresCalendarItem> before) {
        List<ACalendarItem> after = new ArrayList<>();
        if (before == null) {
            return after;
        }

        for (SagresCalendarItem old : before) {
            if (old.getDay() != null || old.getMessage() != null)
                after.add(new ACalendarItem(old.getDay(), old.getMessage()));
        }

        return after;
    }
}
