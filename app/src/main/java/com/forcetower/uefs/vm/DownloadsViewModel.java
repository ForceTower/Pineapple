package com.forcetower.uefs.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.rep.RefreshRepository;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by João Paulo on 23/03/2018.
 */

public class DownloadsViewModel extends ViewModel {
    private final RefreshRepository repository;
    private final AppExecutors executors;
    private final File cacheDir;

    private MediatorLiveData<Resource<Integer>> downloadCertificate;
    private boolean requestEnrollCertificate = false;

    @Inject
    public DownloadsViewModel(RefreshRepository repository, AppExecutors executors, Context context) {
        this.repository = repository;
        this.executors = executors;
        this.cacheDir = context.getCacheDir();
        downloadCertificate = new MediatorLiveData<>();
    }

    public MediatorLiveData<Resource<Integer>> getDownloadCertificate() {
        return downloadCertificate;
    }

    public void triggerDownloadCertificate() {
        if (!requestEnrollCertificate) {
            requestEnrollCertificate = true;
            LiveData<Resource<Integer>> downloadRes = repository.loginAndDownloadEnrollmentCertificate();
            downloadCertificate.addSource(downloadRes, resource -> {
                //noinspection ConstantConditions
                if (resource.status != Status.LOADING) {
                    downloadCertificate.removeSource(downloadRes);
                    requestEnrollCertificate = false;
                }
                downloadCertificate.postValue(resource);
            });
        }
    }

    public void saveBitmap(Bitmap bitmap) {
        executors.diskIO().execute(() -> {
            File file = new File(cacheDir, "profile_image.jpg");
            if (bitmap == null) {
                file.delete();
                return;
            }

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public LiveData<Bitmap> getProfileImage() {
        MutableLiveData<Bitmap> data = new MediatorLiveData<>();
        executors.diskIO().execute(() -> {
            try {
                File file = new File(cacheDir, "profile_image.jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                data.postValue(bitmap);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                data.postValue(null);
            }
        });
        return data;
    }
}
