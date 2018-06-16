package com.forcetower.uefs.vm.service;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.service.EventRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 15/06/2018.
 */
public class EventsViewModel extends ViewModel {
    private final EventRepository repository;

    private LiveData<Resource<List<Event>>> eventSrc;
    private Event currentEvent;

    @Inject
    EventsViewModel(EventRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<Event>>> getEvents() {
        if (eventSrc == null) eventSrc = repository.getEvents();
        return eventSrc;
    }

    public Event getCurrentEvent() {
        if (currentEvent == null) currentEvent = new Event();
        return currentEvent;
    }
}
