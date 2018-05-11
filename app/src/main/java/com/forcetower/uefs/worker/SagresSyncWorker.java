package com.forcetower.uefs.worker;

import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.forcetower.uefs.BuildConfig;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.db.dao.GradeInfoDao;
import com.forcetower.uefs.db.dao.MessageDao;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.GradeInfo;
import com.forcetower.uefs.db.entity.GradeSection;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.db.entity.Semester;
import com.forcetower.uefs.db_service.entity.UpdateStatus;
import com.forcetower.uefs.ntf.NotificationCreator;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.rep.sgrs.RefreshRepository;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.UNEService;
import com.forcetower.uefs.util.NetworkUtils;

import java.util.List;

import javax.inject.Inject;

import androidx.work.Worker;
import timber.log.Timber;

/**
 * Created by João Paulo on 10/05/2018.
 */
public class SagresSyncWorker extends Worker {
    private UEFSApplication.RefreshObjects objects;
    private SharedPreferences preferences;
    private LiveData<Resource<Integer>> call;
    private LiveData<ApiResponse<UpdateStatus>> updateData;

    @NonNull
    @Override
    public WorkerResult doWork() {
        objects = ((UEFSApplication)getApplicationContext()).getRefreshPackage();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        initiateSync();
        return WorkerResult.SUCCESS;
    }

    private void initiateSync() {
        if (!initialVerifications()) {
            return;
        }

        objects.executors.networkIO().execute(() -> {
            if (BuildConfig.DEBUG) {
                proceedSync();
            } else {
                updateData = objects.service.getUpdateStatus();
                updateData.observeForever(this::updateAccountObserver);
            }
        });
    }

    private void updateAccountObserver(ApiResponse<UpdateStatus> updateResp) {
        if (updateResp == null) {
            Timber.d("Won't sync: null response");
            return;
        }

        objects.executors.mainThread().execute(() -> updateData.removeObserver(this::updateAccountObserver));

        if (!updateResp.isSuccessful()) {
            Timber.d("Won't sync: unsuccessful response, code %d", updateResp.code);
            return;
        }

        UpdateStatus status = updateResp.body;
        if (status == null) {
            Timber.d("Won't sync: object status is null");
            return;
        }

        if (!status.isAlarm()) {
            Timber.d("Won't sync: alarm is disabled");
            return;
        }

        proceedSync();
    }

    private void proceedSync() {
        Timber.d("Application is online [WORKER]");
        objects.executors.diskIO().execute(() -> {
            Access access = objects.database.accessDao().getAccessDirect();
            if (access == null) {
                Timber.d("Access is null... stop");
                NotificationCreator.createNotConnectedNotification(getApplicationContext());
            } else {
                objects.database.profileDao().setLastSyncAttempt(System.currentTimeMillis());
                objects.database.messageDao().clearAllNotifications();
                objects.database.gradeInfoDao().clearAllNotifications();
                objects.executors.mainThread().execute(() -> {
                    call = objects.repository.refreshData();
                    call.observeForever(this::progressObserver);
                });
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
            Timber.d("Refresh progress on alarm: %s", getApplicationContext().getString(resource.data));
        }
    }

    private void createNotifications() {
        objects.executors.diskIO().execute(() -> {
            messagesNotifications();
            gradesNotifications();
        });
    }

    private void messagesNotifications() {
        MessageDao messageDao = objects.database.messageDao();
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
        List<Semester> semesters = objects.database.semesterDao().getAllSemestersDirect();
        Semester semester = Semester.getCurrentSemester(semesters);
        String name = semester.getName();

        GradeInfoDao infoDao = objects.database.gradeInfoDao();
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
        GradeSection section = objects.database.gradeSectionDao().getSectionByIdDirect(info.getSection());
        Discipline discipline = objects.database.disciplineDao().getDisciplinesByIdDirect(section.getDiscipline());
        info.setClassName(discipline.getName());
    }
}
