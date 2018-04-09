package com.forcetower.uefs.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.WorkerThread;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.CalendarEvent;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.rep.ScheduleRepository;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.ColorDefinition;
import com.google.api.services.calendar.model.Colors;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private MediatorLiveData<Resource<Integer>> resetData;
    private Calendar service;

    private boolean exporting = false;

    @Inject
    public GoogleCalendarViewModel(AppExecutors executors, ScheduleRepository repository) {
        this.executors = executors;
        this.repository = repository;
        this.exportData = new MediatorLiveData<>();
        this.resetData = new MediatorLiveData<>();
    }

    public LiveData<Resource<Integer>> getExportData() {
        return exportData;
    }

    public void exportData(GoogleAccountCredential credential) {
        exporting = true;
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
                exporting = false;
            } else {
                String[] parts = group.getClassPeriod().split("até");
                String start = reformatDate(parts[0], false);
                String end = reformatDate(parts[1], true);
                if (parts.length == 2 && start != null && end != null) {
                    LiveData<List<DisciplineClassLocation>> discSrc = repository.getSchedule(null);
                    exportData.addSource(discSrc, disciplines -> {
                        exportData.removeSource(discSrc);
                        LiveData<Resource<Integer>> singleSrc = resetExportedSchedule(credential, true);
                        exportData.addSource(singleSrc, resource -> {
                            //noinspection ConstantConditions
                            if (resource.status != Status.LOADING) {
                                exportData.removeSource(singleSrc);
                                //noinspection ConstantConditions;
                                executors.others().execute(() -> literalExport(disciplines, start, end));
                            }
                        });
                    });
                } else {
                    exportData.postValue(Resource.error("Invalid amount of stuff", R.string.exporter_not_2_parts));
                    exporting = false;
                }
            }
        });
    }

    @WorkerThread
    private void literalExport(List<DisciplineClassLocation> disciplines, String smtStart, String smtEnd) {
        exportData.postValue(Resource.loading(R.string.preparing_schedule_to_export));

        List<String> colorDef = new ArrayList<>();
        try {
            Colors colors = service.colors().get().execute();
            for (Map.Entry<String, ColorDefinition> entry : colors.getCalendar().entrySet()) {
                colorDef.add(entry.getKey());
            }
        } catch (Exception e) {
            Timber.d("List of colors were rejected");
        }

        List<Event> events = new ArrayList<>();
        int index = 0;
        HashMap<String, Integer> classIndexer = new HashMap<>();
        for (DisciplineClassLocation location : disciplines) {
            try {
                Event event = new Event();
                event.setSummary("[" + location.getClassCode() + "] " + location.getClassName());
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

                if (colorDef.size() > 1) {
                    int value;
                    if (classIndexer.containsKey(location.getClassCode())) {
                        value = classIndexer.get(location.getClassCode());
                    } else {
                        index++;
                        value = (index%(colorDef.size()-1));
                        classIndexer.put(location.getClassCode(), value);
                    }
                    String def = colorDef.get(value);
                    event.setColorId(def);
                }

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
            int i = 0;
            for (Event event : events) {
                try {
                    Event inserted = service.events().insert(calendarId, event).execute();
                    if (inserted != null && inserted.getId() != null) {
                        exported.add(inserted);
                        executors.diskIO().execute(() -> repository.insertCalendarEvent(calendarId, inserted.getId(), ""));
                        Timber.d("Exported class, ID: %s", inserted.getId());
                    }
                    i++;
                    Timber.d("Percentage: %d", ((i*100)/events.size()));
                    exportData.postValue(Resource.success((i*100)/events.size()));
                } catch (GooglePlayServicesAvailabilityIOException e) {
                    exportData.postValue(Resource.error("GooglePlayServicesAvailabilityIOException Exception", 350, e));
                    return;
                } catch (UserRecoverableAuthIOException e) {
                    exportData.postValue(Resource.error("User recoverable Exception", 360, e));
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            exporting = false;
            exportData.postValue(Resource.success(1000));
            Timber.d("Number of exported events: %d", exported.size());
        });
    }

    public boolean isExporting() {
        return exporting;
    }

    public LiveData<Resource<Integer>> getResetData() {
        return resetData;
    }

    public LiveData<Resource<Integer>> resetExportedSchedule(GoogleAccountCredential credential, boolean single) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        service = new Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("UNES")
                .build();

        MediatorLiveData<Resource<Integer>> value;
        if (single) value = new MediatorLiveData<>();
        else value = resetData;

        LiveData<List<CalendarEvent>> eventSrc = repository.getExportedCalendar();

        value.addSource(eventSrc, events -> {
            value.removeSource(eventSrc);
            executors.networkIO().execute(() -> {
                int i = 0;
                //noinspection ConstantConditions
                Timber.d("Events size is %d", events.size());
                for (CalendarEvent event : events) {
                    i++;
                    Timber.d("Event %s", event);
                    try {
                        service.events().delete(event.getCalendarId(), event.getEventId()).execute();
                        executors.diskIO().execute(() -> repository.deleteCalendarEvent(event));
                        Timber.d("Removed event from calendar");
                        value.postValue(Resource.loading((i*100)/events.size()));
                    } catch (Exception e) {
                        Timber.d("Failed to delete event due to %s ", e.getMessage());
                    }
                }
                value.postValue(Resource.success(1000));
            });
        });

        return value;
    }
}
