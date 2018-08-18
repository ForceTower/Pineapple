package com.forcetower.uefs.work.sync;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.forcetower.uefs.work.event.CreateEventWorker;
import com.forcetower.uefs.work.event.EventApprovalWorker;
import com.forcetower.uefs.work.grades.DownloadGradesWorker;

public class SyncWorkCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case SagresSyncWorker.TAG:
                return new SagresSyncWorker();
            case DownloadGradesWorker.TAG:
                return new DownloadGradesWorker();
            case CreateEventWorker.TAG:
                return new CreateEventWorker();
            case EventApprovalWorker.TAG:
                return new EventApprovalWorker();
        }
        return null;
    }
}
