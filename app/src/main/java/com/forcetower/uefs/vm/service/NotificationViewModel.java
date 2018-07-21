package com.forcetower.uefs.vm.service;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.db_service.helper.ImGurDataObject;
import com.forcetower.uefs.rep.NotificationRepository;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.util.ImageUtils;
import com.forcetower.uefs.util.VersionUtils;

import java.util.UUID;

import javax.inject.Inject;

import timber.log.Timber;

public class NotificationViewModel extends ViewModel {
    private final MediatorLiveData<Resource<ImGurDataObject>> uploadImGurSrc;
    private final MediatorLiveData<Bitmap> blurImageSrc;
    private final MediatorLiveData<Resource<ActionResult<String>>> sendNotificationSrc;

    private final AppExecutors executors;
    private final NotificationRepository repository;

    private String link;
    private String deleteHash;
    private Uri currentUri;

    @Inject
    NotificationViewModel(AppExecutors executors, NotificationRepository repository) {
        this.executors = executors;
        this.repository = repository;
        this.uploadImGurSrc = new MediatorLiveData<>();
        this.blurImageSrc = new MediatorLiveData<>();
        this.sendNotificationSrc = new MediatorLiveData<>();
    }

    public void setImageUrl(String link) {
        this.link = link;
        Timber.d("Link was set to: " + link);
    }

    public void setDeleteHash(String deleteHash) {
        this.deleteHash = deleteHash;
        Timber.d("Delete hash was set to: " + deleteHash);
    }

    public Uri getCurrentUri() {
        return currentUri;
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

    public void uploadImageToImGur(Bitmap bitmap, Uri uri) {
        this.currentUri = uri;

        LiveData<Resource<ImGurDataObject>> uploadSrc = repository.uploadImageToImGur(bitmap, UUID.randomUUID().toString());
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

    public LiveData<Resource<ImGurDataObject>> getUploadImGur() {
        return uploadImGurSrc;
    }

    public void sendNotification(String title, String message, boolean withImage) {
        LiveData<Resource<ActionResult<String>>> sendSrc = repository.sendNotification(title, message,
                withImage ? link : null,
                withImage ? deleteHash : null
        );

        sendNotificationSrc.addSource(sendSrc, resp -> {
            //noinspection ConstantConditions
            if (resp.status != Status.LOADING) {
                sendNotificationSrc.removeSource(sendSrc);
            }
            sendNotificationSrc.postValue(resp);
        });
    }

    public LiveData<Resource<ActionResult<String>>> getSendNotification() {
        return sendNotificationSrc;
    }
}
