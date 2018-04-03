package com.forcetower.uefs.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.WorkerThread;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.rep.ScheduleRepository;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.util.AbsentLiveData;
import com.forcetower.uefs.util.GeneralUtils;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.forcetower.uefs.util.DateUtils.getNextDay;
import static com.forcetower.uefs.util.DateUtils.reformatDate;
import static com.forcetower.uefs.util.DateUtils.remakeTime;

/**
 * Created by João Paulo on 02/04/2018.
 */
public class GoogleCalendarViewModel extends ViewModel {
    private final AppExecutors executors;
    private final ScheduleRepository repository;

    private MediatorLiveData<Resource<Integer>> exportData;
    private Calendar service;

    @Inject
    public GoogleCalendarViewModel(AppExecutors executors, ScheduleRepository repository) {
        this.executors = executors;
        this.repository = repository;
        this.exportData = new MediatorLiveData<>();
    }

    public LiveData<Resource<Integer>> getExportData() {
        return exportData;
    }

    public void exportData(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        service = new Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("UNES")
                .build();

        exportData.postValue(Resource.loading(R.string.loading_schedule));
        LiveData<DisciplineGroup> groupSrc = repository.getDisciplineWithDetailsLoaded();
        exportData.addSource(groupSrc, group -> {
            exportData.removeSource(groupSrc);

            if (group == null) {
                exportData.postValue(Resource.error("Can only do it when there is at least one class loaded", 500, R.string.export_requires_one_details_loaded_class));
            } else {
                String[] parts = group.getClassPeriod().split("até");
                String start = reformatDate(parts[0], false);
                String end = reformatDate(parts[1], true);
                if (parts.length == 2 && start != null && end != null) {
                    LiveData<List<DisciplineClassLocation>> discSrc = repository.getSchedule(null);
                    exportData.addSource(discSrc, disciplines -> {
                        exportData.removeSource(discSrc);
                        //noinspection ConstantConditions;
                        executors.others().execute(() -> literalExport(disciplines, start, end));
                    });
                } else {
                    exportData.postValue(Resource.error("Invalid amount of stuff", R.string.exporter_not_2_parts));
                }
            }
        });
    }

    @WorkerThread
    private void literalExport(List<DisciplineClassLocation> disciplines, String smtStart, String smtEnd) {
        exportData.postValue(Resource.loading(R.string.preparing_schedule_to_export));

        List<Event> events = new ArrayList<>();
        for (DisciplineClassLocation location : disciplines) {
            try {
                Event event = new Event();
                event.setSummary(location.getClassCode() + " - " + location.getClassName());
                event.setLocation(location.getCampus() + " - " + location.getModulo() + " - " + location.getRoom());
                event.setDescription("Aula de " + location.getClassName());

                String remakeStart = remakeTime(location.getStartTime());
                String remakeEnd = remakeTime(location.getEndTime());

                DateTime start = new DateTime(getNextDay(smtStart, location.getDay()) + "T" + remakeStart);
                DateTime end = new DateTime(getNextDay(smtStart, location.getDay()) + "T" + remakeEnd);

                EventDateTime startTime = new EventDateTime()
                        .setDateTime(start)
                        .setTimeZone("America/Sao_Paulo");
                EventDateTime endTime = new EventDateTime()
                        .setDateTime(end)
                        .setTimeZone("America/Sao_Paulo");

                event.setStart(startTime);
                event.setEnd(endTime);

                String[] recurrence = new String[] {"RRULE:FREQ=WEEKLY;UNTIL=" + smtEnd + "T235959Z"};
                event.setRecurrence(Arrays.asList(recurrence));


                EventReminder[] reminderOverrides = new EventReminder[] {
                        new EventReminder().setMethod("popup").setMinutes(10)
                };

                Event.Reminders reminders = new Event.Reminders()
                        .setUseDefault(false)
                        .setOverrides(Arrays.asList(reminderOverrides));
                event.setReminders(reminders);

                Timber.d("Created event for class %s", location.getClassName());
                events.add(event);
            } catch (Exception e) {
                Timber.d("Failed to export %s due to %s", location.getClassName(), e.getMessage());
                exportData.postValue(Resource.error(location.getClassName(), R.string.failed_to_export_class));
            }
        }
        exportData.postValue(Resource.loading(R.string.exporting_classes));
        String calendarId = "primary";
        executors.networkIO().execute(() -> {
            List<Event> exported = new ArrayList<>();
            for (Event event : events) {
                try {
                    Event inserted = service.events().insert(calendarId, event).execute();
                    if (inserted != null) exported.add(inserted);
                    Timber.d("Exported class");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Timber.d("Number of exported events: %d", exported.size());
        });
    }
}
