package com.forcetower.uefs.worker.mean;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by João Paulo on 20/06/2018.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DisciplineMeanWorker extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
