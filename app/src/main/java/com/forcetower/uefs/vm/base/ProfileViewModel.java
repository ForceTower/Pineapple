package com.forcetower.uefs.vm.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.db.dao.AccessDao;
import com.forcetower.uefs.db.dao.ProfileDao;
import com.forcetower.uefs.db.dao.SemesterDao;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db.entity.Semester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by João Paulo on 08/03/2018.
 */
public class ProfileViewModel extends ViewModel {
    private final ProfileDao profileDao;
    private final SemesterDao semesterDao;
    private final AccessDao accessDao;
    private final AppExecutors executors;
    private final File cacheDir;
    private LiveData<Profile> profileLiveData;
    private LiveData<List<Semester>> semestersLiveData;
    private LiveData<Access> accessLiveData;

    @Inject
    public ProfileViewModel(ProfileDao profileDao, SemesterDao semesterDao, AccessDao accessDao,
                            AppExecutors executors, Context context) {
        this.profileDao = profileDao;
        this.semesterDao = semesterDao;
        this.accessDao = accessDao;
        this.executors = executors;
        this.cacheDir = context.getCacheDir();
    }

    public LiveData<Profile> getProfile() {
        if (profileLiveData == null)
            profileLiveData = profileDao.getProfile();
        return  profileLiveData;
    }

    public LiveData<List<Semester>> getSemesters() {
        if (semestersLiveData == null)
            semestersLiveData = semesterDao.getAllSemesters();
        return semestersLiveData;
    }

    public LiveData<Access> getAccess() {
        if (accessLiveData == null)
            accessLiveData = accessDao.getAccess();
        return accessLiveData;
    }

    public LiveData<Bitmap> getProfileImage() {
        MutableLiveData<Bitmap> data = new MediatorLiveData<>();
        executors.diskIO().execute(() -> {
            try {
                File file = new File(cacheDir, "profile_image.jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                int height = bitmap.getHeight();
                int width  = bitmap.getWidth();

                int proportion = height/512;
                if (proportion == 0) proportion = 1;
                bitmap = Bitmap.createScaledBitmap(bitmap, width/proportion, height/proportion, false);
                data.postValue(bitmap);
            }
            catch (FileNotFoundException e) {
                Timber.d("File doesn't exists");
                data.postValue(null);
            }
        });
        return data;
    }

    public void saveProfileImageBitmap(Bitmap bitmap) {
        executors.diskIO().execute(() -> {
            File file = new File(cacheDir, "profile_image.jpg");
            if (bitmap == null) {
                file.delete();
                return;
            }

            FileOutputStream fos = null;
            try {
                int height = bitmap.getHeight();
                int width  = bitmap.getWidth();

                int proportion = height/512;
                if (proportion == 0) proportion = 1;
                Bitmap save = Bitmap.createScaledBitmap(bitmap, width/proportion, height/proportion, false);

                fos = new FileOutputStream(file);
                save.compress(Bitmap.CompressFormat.JPEG, 50, fos);
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

    public void setProfileCourse(String name) {
        executors.others().execute(() -> profileDao.setProfileCourse(name));
    }
}
