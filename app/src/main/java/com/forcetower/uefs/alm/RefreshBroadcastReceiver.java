package com.forcetower.uefs.alm;

import android.arch.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.BuildConfig;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.dao.GradeInfoDao;
import com.forcetower.uefs.db.dao.MessageDao;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.GradeInfo;
import com.forcetower.uefs.db.entity.GradeSection;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db.entity.Semester;
import com.forcetower.uefs.db.entity.SyncRegistry;
import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.db_service.entity.Course;
import com.forcetower.uefs.db_service.entity.UpdateStatus;
import com.forcetower.uefs.ntf.NotificationCreator;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.rep.sgrs.RefreshRepository;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.UNEService;
import com.forcetower.uefs.util.NetworkUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Response;
import timber.log.Timber;

public class RefreshBroadcastReceiver extends BroadcastReceiver {
    @Inject
    AppDatabase uDatabase;
    @Inject
    AppExecutors executors;
    @Inject
    ServiceDatabase sDatabase;
    @Inject
    UNEService service;
    @Inject
    RefreshRepository repository;

    private Context context;

    private LiveData<Resource<Integer>> call;
    private android.arch.lifecycle.LiveData<ApiResponse<UpdateStatus>> updateData;
    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        executors.diskIO().execute(() -> {
            SyncRegistry registry = new SyncRegistry(System.currentTimeMillis());
            uDatabase.syncRegistryDao().insert(registry);
            initiateSync();
        });
    }

    private void initiateSync() {
        if (!initialVerifications()) {
            return;
        }

        executors.networkIO().execute(() -> {
            if (BuildConfig.DEBUG) {
                proceedSync();
            } else {
                updateData = service.getUpdateStatus();
                updateData.observeForever(this::updateAccountObserver);
            }
        });
    }

    private void updateAccountObserver(ApiResponse<UpdateStatus> updateResp) {
        if (updateResp == null) {
            executors.diskIO().execute(() -> uDatabase.syncRegistryDao().updateReason(1));
            Timber.d("Won't sync: null response");
            return;
        }

        executors.mainThread().execute(() -> updateData.removeObserver(this::updateAccountObserver));

        if (!updateResp.isSuccessful()) {
            Timber.d("Will sync: unsuccessful response, code %d", updateResp.code);
            proceedSync();
            return;
        }

        UpdateStatus status = updateResp.body;
        if (status == null) {
            Timber.d("Will sync: object status is null");
            proceedSync();
            return;
        }

        if (!status.isAlarm()) {
            executors.diskIO().execute(() -> uDatabase.syncRegistryDao().updateReason(2));
            Timber.d("Won't sync: worker is disabled");
            return;
        }

        proceedSync();
    }

    private void proceedSync() {
        Timber.d("Application is online [WORKER]");
        executors.diskIO().execute(() -> {
            try {
                Access access = uDatabase.accessDao().getAccessDirect();
                if (access == null) {
                    Timber.d("Access is null... stop");
                    executors.diskIO().execute(() -> uDatabase.syncRegistryDao().updateReason(3));
                    NotificationCreator.createNotConnectedNotification(context);
                } else {
                    uDatabase.profileDao().setLastSyncAttempt(System.currentTimeMillis());
                    uDatabase.messageDao().clearAllNotifications();
                    uDatabase.gradeInfoDao().clearAllNotifications();
                    executors.mainThread().execute(() -> {
                        call = repository.refreshData();
                        call.observeForever(this::progressObserver);
                    });
                }
            } catch (Exception e) {
                Timber.e("Ignored Exception");
                e.printStackTrace();
            }
        });
    }

    private boolean initialVerifications() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            executors.diskIO().execute(() -> uDatabase.syncRegistryDao().updateReason(4));
            Timber.d("No internet");
            return false;
        }

        if (preferences.getBoolean("sync_wifi_only", false) && !NetworkUtils.isConnectedToWifi(context)) {
            Timber.d("Not on a wifi connection");
            executors.diskIO().execute(() -> uDatabase.syncRegistryDao().updateReason(5));
            return false;
        }

        return true;
    }

    private void progressObserver(Resource<Integer> resource) {
        if (resource.status == Status.SUCCESS) {
            call.removeObserver(this::progressObserver);
            createNotifications();
        } else if (resource.status == Status.ERROR) {
            call.removeObserver(this::progressObserver);
            Timber.d("Failed on refresh");
            createNotifications();
        } else {
            //noinspection ConstantConditions
            Timber.d("Refresh progress on job: %s", context.getString(resource.data));
        }
    }

    private void createNotifications() {
        executors.diskIO().execute(() -> {
            messagesNotifications();
            gradesNotifications();

            try {
                setupData();
                sendCourse();
            } catch (Throwable t) {
                Timber.d("Happened before");
                Crashlytics.logException(t);
            }
        });
    }

    private void sendCourse() {
        try {
            Access a = uDatabase.accessDao().getAccessDirect();
            Profile p = uDatabase.profileDao().getProfileDirect();
            if (a != null && p != null && p.getCourse() != null) {
                if (p.getCourseReference() <= 1) {
                    List<Course> courses = sDatabase.courseDao().getAllCoursesDirect();
                    int match = 0;
                    for (Course course : courses) {
                        if (course.getName().equalsIgnoreCase(p.getCourse())) {
                            match = course.getServiceId();
                            break;
                        }
                    }
                    if (match > 0) {
                        uDatabase.profileDao().setProfileCourseId(match);
                        p.setCourseReference(match);
                    }
                }

                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    String token = task.getResult().getToken();
                    executors.networkIO().execute(() -> {
                        try {
                            Response response = service.postFirebaseToken(a.getUsername(), token, p.getCourseReference()).execute();
                            if (response.isSuccessful()) {
                                Timber.d("Success Setting Course");
                            } else {
                                Timber.d("Failed Setting Course");
                                Timber.d("Response code: " + response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });
            } else if (a != null) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    String token = task.getResult().getToken();
                    executors.networkIO().execute(() -> {
                        try {
                            Response response = service.postFirebaseToken(a.getUsername(), token).execute();
                            if (response.isSuccessful()) {
                                Timber.d("Success Setting Course");
                            } else {
                                Timber.d("Failed Setting Course");
                                Timber.d("Response code: " + response.code());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });
            }
        } catch (Throwable t) {
            Timber.d("A throwable happened");
            Crashlytics.logException(t);
        }
    }

    private void setupData() {
        Access a = uDatabase.accessDao().getAccessDirect();
        Profile p = uDatabase.profileDao().getProfileDirect();
        List<Semester> semesters = uDatabase.semesterDao().getAllSemestersDirect();
        if (a != null) {
            Crashlytics.setUserIdentifier(a.getUsername());
            Crashlytics.setUserName(p != null ? p.getName() : "Undefined");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("firebase_tokens");
            try {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(value -> {
                    if (value.isSuccessful()) {
                        String token = value.getResult().getToken();
                        reference.child(a.getUsernameFixed()).child("token").setValue(token);
                        reference.child(a.getUsernameFixed()).child("device").setValue(Build.MANUFACTURER + " " + Build.MODEL);
                        reference.child(a.getUsernameFixed()).child("android").setValue(Build.VERSION.SDK_INT);
                        reference.child(a.getUsernameFixed()).child("name").setValue(p != null ? p.getName() : "Null Profile");

                        if (p != null && p.getCourse() != null) {
                            DatabaseReference courses = FirebaseDatabase.getInstance().getReference("courses").child(p.getCourseFixed());
                            courses.child(a.getUsernameFixed()).child("name").setValue(p.getName());
                            courses.child(a.getUsernameFixed()).child("score").setValue(p.getScore());
                            courses.child(a.getUsernameFixed()).child("semester").setValue(semesters.size());
                        }
                    } else {
                        Timber.d("Failed");
                    }

                });
            } catch (Throwable t) {
                Crashlytics.logException(t);
                Timber.d("A throwable just happened: " + t.getMessage());
                t.printStackTrace();
            }
        }
    }

    private void messagesNotifications() {
        MessageDao messageDao = uDatabase.messageDao();
        Timber.d("Generate message notifications");
        List<Message> messages = messageDao.getAllUnnotifiedMessages();
        for (Message message : messages) {
            boolean notified = NotificationCreator.createMessageNotification(context, message);
            if (notified) {
                message.setNotified(1);
            }
        }
        if (!messages.isEmpty()) {
            Message[] array = new Message[messages.size()];
            messages.toArray(array);
            messageDao.insertMessages(array);
        }
    }

    private void gradesNotifications() {
        List<Semester> semesters = uDatabase.semesterDao().getAllSemestersDirect();
        Semester semester = Semester.getCurrentSemester(semesters);
        String name = semester.getName();

        GradeInfoDao infoDao = uDatabase.gradeInfoDao();
        Timber.d("Generate grades notifications for grades posted");
        gradesNotificationHandler(infoDao, infoDao.getUnnotifiedGrades(name), 1);
        Timber.d("Generate grades notifications for recently created grades");
        gradesNotificationHandler(infoDao, infoDao.getRecentlyCreatedGrades(name), 2);
        Timber.d("Generate grades notifications for changed grades");
        gradesNotificationHandler(infoDao, infoDao.getAvDateChangedGrades(name), 3);
    }

    private void gradesNotificationHandler(GradeInfoDao infoDao, List<GradeInfo> infos, int type) {
        Timber.d("Number of notifications: %d", infos.size());
        for (GradeInfo info : infos) {
            findClass(info);
            boolean notified = NotificationCreator.createGradeNotification(context, info, type);
            if (notified) info.setNotified(0);
        }

        if (!infos.isEmpty()) {
            Timber.d("This should be the same error: %s", infos);
            GradeInfo[] array = new GradeInfo[infos.size()];
            infos.toArray(array);
            for (GradeInfo info : array) {
                Timber.d("item: %d", info.getUid());
            }
            infoDao.insertGradeInfo(array);
        }
    }

    private void findClass(GradeInfo info) {
        GradeSection section = uDatabase.gradeSectionDao().getSectionByIdDirect(info.getSection());
        Discipline discipline = uDatabase.disciplineDao().getDisciplinesByIdDirect(section.getDiscipline());
        info.setClassName(discipline.getName());
    }
}
