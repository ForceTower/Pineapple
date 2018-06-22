package com.forcetower.uefs.worker;

import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
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
import timber.log.Timber;

/**
 * Created by João Paulo on 17/05/2018.
 */
public class SagresSyncJobService extends JobService {
    @Inject
    RefreshRepository repository;
    @Inject
    AppDatabase database;
    @Inject
    AppExecutors executors;
    @Inject
    UNEService service;

    private LiveData<Resource<Integer>> call;
    private LiveData<ApiResponse<UpdateStatus>> updateData;
    private SharedPreferences preferences;
    boolean completed = false;
    private JobParameters jobParameters;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters job) {
        Timber.d("Job Started - FirebaseJobDispatcher");
        if (BuildConfig.DEBUG) NotificationCreator.createSyncWarning(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        jobParameters = job;
        initiateSync();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Timber.d("Job Finished - FirebaseJobDispatcher");
        return false;
    }

    private void initiateSync() {
        if (!initialVerifications()) {
            completed = true;
            jobFinished(jobParameters, false);
            return;
        }

        executors.networkIO().execute(() -> {
            database.profileDao().setLastSyncAttempt(System.currentTimeMillis());
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
            Timber.d("Won't sync: null response");
            completed = true;
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

        if (!status.isWorker()) {
            Timber.d("Won't sync: worker is disabled");
            completed = true;
            return;
        }

        proceedSync();
    }

    private void proceedSync() {
        Timber.d("Application is online [WORKER]");
        executors.diskIO().execute(() -> {
            try {
                Access access = database.accessDao().getAccessDirect();
                if (access == null) {
                    Timber.d("Access is null... stop");
                    NotificationCreator.createNotConnectedNotification(getApplicationContext());
                    completed = true;
                } else {
                    database.profileDao().setLastSyncAttempt(System.currentTimeMillis());
                    database.messageDao().clearAllNotifications();
                    database.gradeInfoDao().clearAllNotifications();
                    executors.mainThread().execute(() -> {
                        call = repository.refreshData();
                        call.observeForever(this::progressObserver);
                    });
                }
            } catch (Exception ignored) {
                Timber.e("Ignored Exception");
                ignored.printStackTrace();
                jobFinished(jobParameters, false);
            }
        });
    }

    private boolean initialVerifications() {
        if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            Timber.d("No internet");
            return false;
        }

        if (preferences.getBoolean("sync_wifi_only", false) && !NetworkUtils.isConnectedToWifi(getApplicationContext())) {
            Timber.d("Not on a wifi connection");
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
            Timber.d("Refresh progress on job: %s", getApplicationContext().getString(resource.data));
        }
    }

    private void createNotifications() {
        executors.diskIO().execute(() -> {
            messagesNotifications();
            gradesNotifications();
            completed = true;

            setupData();
            jobFinished(jobParameters, false);
        });
    }

    private void setupData() {
        Access a = database.accessDao().getAccessDirect();
        Profile p = database.profileDao().getProfileDirect();
        if (a != null) {
            Crashlytics.setUserIdentifier(a.getUsername());
            Crashlytics.setUserName(p != null ? p.getName() : "Undefined");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("firebase_tokens");
            String token = FirebaseInstanceId.getInstance().getToken();
            if (token != null) {
                reference.child(a.getUsernameFixed()).child("token").setValue(token);
                reference.child(a.getUsernameFixed()).child("device").setValue(Build.MANUFACTURER + " " + Build.MODEL);
                reference.child(a.getUsernameFixed()).child("android").setValue(Build.VERSION.SDK_INT);
                reference.child(a.getUsernameFixed()).child("name").setValue(p != null ? p.getName() : "Null Profile");

                List<Semester> semesters = database.semesterDao().getAllSemestersDirect();
                if (p != null && p.getCourse() != null) {
                    DatabaseReference courses = FirebaseDatabase.getInstance().getReference("courses").child(p.getCourseFixed());
                    courses.child(a.getUsernameFixed()).child("name").setValue(p.getName());
                    courses.child(a.getUsernameFixed()).child("score").setValue(p.getScore());
                    courses.child(a.getUsernameFixed()).child("semester").setValue(semesters.size());
                }
            }
        }
    }

    private void messagesNotifications() {
        MessageDao messageDao = database.messageDao();
        Timber.d("Generate message notifications");
        List<Message> messages = messageDao.getAllUnnotifiedMessages();
        for (Message message : messages) {
            boolean notified = NotificationCreator.createMessageNotification(getApplicationContext(), message);
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
        List<Semester> semesters = database.semesterDao().getAllSemestersDirect();
        Semester semester = Semester.getCurrentSemester(semesters);
        String name = semester.getName();

        GradeInfoDao infoDao = database.gradeInfoDao();
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
            boolean notified = NotificationCreator.createGradeNotification(getApplicationContext(), info, type);
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
        GradeSection section = database.gradeSectionDao().getSectionByIdDirect(info.getSection());
        Discipline discipline = database.disciplineDao().getDisciplinesByIdDirect(section.getDiscipline());
        info.setClassName(discipline.getName());
    }
}
