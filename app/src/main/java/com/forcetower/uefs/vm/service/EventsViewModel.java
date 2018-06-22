package com.forcetower.uefs.vm.service;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.db_service.helper.ImGurDataObject;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.rep.service.EventRepository;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.util.ImageUtils;
import com.forcetower.uefs.util.VersionUtils;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by João Paulo on 15/06/2018.
 */
public class EventsViewModel extends ViewModel {
    private final EventRepository repository;
    private final AppExecutors executors;
    private final MediatorLiveData<Resource<ImGurDataObject>> uploadImGurSrc;
    private final MediatorLiveData<Bitmap> blurImageSrc;
    private final MediatorLiveData<Resource<ActionResult<Event>>> createEventSrc;
    private final MutableLiveData<Boolean> sendingBooleanSrc;

    private LiveData<Resource<List<Event>>> eventSrc;
    private LiveData<Resource<List<Event>>> eventUnapprovedSrc;

    private Uri currentImageUri;
    private Event currentEvent;

    @Inject
    EventsViewModel(EventRepository repository, AppExecutors executors) {
        this.repository = repository;
        this.executors = executors;
        this.uploadImGurSrc = new MediatorLiveData<>();
        this.blurImageSrc = new MediatorLiveData<>();
        this.createEventSrc = new MediatorLiveData<>();
        this.sendingBooleanSrc = new MutableLiveData<>();
        this.sendingBooleanSrc.postValue(false);
    }

    public LiveData<Resource<List<Event>>> getEvents() {
        if (eventSrc == null) eventSrc = repository.getEvents();
        return eventSrc;
    }

    public LiveData<Resource<ImGurDataObject>> getUploadImGur() {
        return uploadImGurSrc;
    }

    public Event getCurrentEvent() {
        if (currentEvent == null) {
            currentEvent = new Event();
            currentEvent.setUuid(UUID.randomUUID().toString());
        }
        return currentEvent;
    }

    public void uploadImageToImGur(Bitmap bitmap, Uri uri) {
        this.currentImageUri = uri;
        if (currentEvent == null) Crashlytics.log("Attempt to upload image with event being null");

        LiveData<Resource<ImGurDataObject>> uploadSrc = repository.uploadImageToImGur(bitmap,
                currentEvent != null ? currentEvent.getName() : UUID.randomUUID().toString());
        uploadImGurSrc.addSource(uploadSrc, uploadRes -> {
            uploadImGurSrc.postValue(uploadRes);
            //noinspection ConstantConditions
            if (uploadRes.status != Status.LOADING) {
                uploadImGurSrc.removeSource(uploadSrc);
            }
        });
    }

    public LiveData<Bitmap> getBlurImage() {
        return blurImageSrc;
    }

    public void blurImage(Context context, Bitmap bitmap) {
        if (VersionUtils.isJellyBeanMR1()) {
            LiveData<Bitmap> bitmapSrc = ImageUtils.blurImageAsync(context, bitmap, 20, executors);
            blurImageSrc.addSource(bitmapSrc, value -> {
                blurImageSrc.removeSource(bitmapSrc);
                blurImageSrc.postValue(value);
            });
        }
    }

    public Uri getCurrentUri() {
        return currentImageUri;
    }

    public void setCurrentEvent(Event event) {
        this.currentEvent = event;
    }

    public LiveData<Resource<List<Event>>> getUnapprovedEvents() {
        if (eventUnapprovedSrc == null) eventUnapprovedSrc = repository.getUnapprovedEvents();
        return eventUnapprovedSrc;
    }
}
