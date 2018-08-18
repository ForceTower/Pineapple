package com.forcetower.uefs.work.grades;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.Constants;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.DisciplineMissedClass;
import com.forcetower.uefs.db.entity.Grade;
import com.forcetower.uefs.sgrs.parsers.SagresGradeParser;
import com.forcetower.uefs.sgrs.parsers.SagresMissedClassesParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import javax.inject.Inject;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

import static com.forcetower.uefs.rep.helper.RequestCreator.getGradesFor;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeApprovalRequest;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeApprovalRequestBody;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeGradesRequest;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeLoginRequest;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeRequestBody;
import static com.forcetower.uefs.rep.resources.LoginOnlyResource.isConnected;
import static com.forcetower.uefs.rep.resources.LoginOnlyResource.needApproval;
import static com.forcetower.uefs.rep.sgrs.LoginRepository.defineGrades;
import static com.forcetower.uefs.rep.sgrs.LoginRepository.defineMissedClasses;
import static com.forcetower.uefs.rep.sgrs.LoginRepository.redefinePages;

/**
 * Created by João Paulo on 21/06/2018.
 */
public class DownloadGradesWorker extends Worker {

    public static void createWorker(String semester) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data data = new Data.Builder()
                .putString("semester", semester)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DownloadGradesWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .addTag(Constants.WORKER_DOWNLOAD_GRADES + semester)
                .addTag(Constants.WORKER_DOWNLOAD_GRADES_GENERAL)
                .build();

        WorkManager.getInstance().enqueue(request);
        Timber.d("Created DownloadGradesWorker for " + semester);
    }

    public static void createWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data data = new Data.Builder()
                .putBoolean("key_finder", true)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DownloadGradesWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .addTag(Constants.WORKER_DOWNLOAD_GRADES + "key_find")
                .addTag(Constants.WORKER_DOWNLOAD_GRADES_GENERAL)
                .build();

        WorkManager.getInstance().enqueue(request);
        Timber.d("Created DownloadGradesWorker for key finding");
    }

    @Inject
    AppDatabase mDatabase;
    @Inject
    OkHttpClient mClient;

    private Charset mCharset = Charset.forName("ISO-8859-1");
    private String mSemester;
    private Access mAccess;
    private boolean keyFinder;

    public static void disableWorkers() {
        WorkManager.getInstance().cancelAllWorkByTag(Constants.WORKER_DOWNLOAD_GRADES_GENERAL);
    }

    @NonNull
    @Override
    public Result doWork() {
        ((UEFSApplication)getApplicationContext()).getAppComponent().inject(this);

        mSemester = getInputData().getString("semester");
        if (mSemester == null) {
            keyFinder = getInputData().getBoolean("key_finder", false);
            if (!keyFinder) {
                Timber.d("Nothing to do here");
                return Result.SUCCESS;
            } else {
                Timber.d("Execute worker - Find semesters keys");
            }
        } else {
            Timber.d("Execute worker - Fetch Grades of Semester ID: %s", mSemester);
        }

        mAccess = mDatabase.accessDao().getAccessDirect();
        if (mAccess == null) {
            Timber.e("Access is null - Worker Cancelled - Semester: %s", mSemester);
            return Result.FAILURE;
        }

        try {
            //login();
            findAndFetchGrades();
        } catch (WorkerException e) {
            return e.result;
        } catch (Throwable t) {
            //No exception shall propagate away from here
            t.printStackTrace();
            Crashlytics.logException(t);
            return Result.FAILURE;
        }

        return Result.SUCCESS;
    }

    public void login() throws WorkerException {
        RequestBody body = makeRequestBody(mAccess.getUsername(), mAccess.getPassword());
        Request request = makeLoginRequest(body);

        Call call = mClient.newCall(request);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                String html = response.body().string();
                Document document = Jsoup.parse(html);
                document.charset(mCharset);

                if (!isConnected(document))
                    throw new WorkerException("Worked reports: not connected", Result.FAILURE);

                if (needApproval(document)) {
                    approve(response, document);
                } else {
                    Timber.d("Don't need approval");
                }
            } else {
                throw new WorkerException("Unsuccessful response. Code: " + response.code(), Result.RETRY);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new WorkerException(e.getMessage(), Result.RETRY);
        }
    }

    private void findAndFetchGrades() throws WorkerException {
        Request request = makeGradesRequest();
        Call call = mClient.newCall(request);

        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                String html = response.body().string();
                Document document = Jsoup.parse(html);
                document.charset(mCharset);
                if (!keyFinder)
                    prepareFinalRequest(document);
                else
                    findKeysAndCreateWorks(document);
            } else {
                throw new WorkerException("Unsuccessful response. Code: " + response.code(), Result.RETRY);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new WorkerException(e.getMessage(), Result.RETRY);
        }
    }

    private void findKeysAndCreateWorks(Document document) {
        Elements values = document.select("option");
        for (Element element : values) {
            String value = element.attr("value");
            Timber.d("Setting semester id %s to semester %s", value, element.text().trim());
            mDatabase.semesterDao().setUefsId(value, element.text().trim());
            DownloadGradesWorker.createWorker(value);
        }
    }

    private void prepareFinalRequest(Document document) throws WorkerException {
        Timber.d("Preparing for launch");
        Request request = getGradesFor(mSemester, document);
        Call call = mClient.newCall(request);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                String html = response.body().string();
                Document grades = Jsoup.parse(html);
                grades.charset(mCharset);
                parseAndSaveGrades(grades);
            } else {
                throw new WorkerException("Unsuccessful response. Code: " + response.code(), Result.RETRY);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new WorkerException(e.getMessage(), Result.RETRY);
        }
    }

    private void parseAndSaveGrades(Document document) throws WorkerException {
        Timber.d("Saving Information");
        String semester = SagresGradeParser.getPageSemester(document);
        if (semester == null) {
            Crashlytics.log("Unable to find the semester...");
            Timber.d("Unable to parse grades on page");
            throw new WorkerException("Unable to parse grades on page", Result.RETRY);
        }
        Timber.d("Semester is: %s. Good luck!", semester);
        List<Grade> grades = SagresGradeParser.getGrades(document);
        Timber.d("Grades List Size: " + grades.size());
        if (grades.isEmpty()) {
            throw new WorkerException("No grades for semester " + mSemester + ". Retrying...", Result.RETRY);
        }

        redefinePages(semester, grades, mDatabase);
        defineGrades(semester, grades, mDatabase);
        Pair<Boolean, List<DisciplineMissedClass>> missedClasses = SagresMissedClassesParser.getMissedClasses(document);
        if (!missedClasses.first) {
            defineMissedClasses(semester, missedClasses.second, mDatabase);
        } else {
            Timber.d("Missed classes error");
        }
        Timber.d("All Saved");
    }

    private void approve(Response response, Document document) throws WorkerException {
        URL respUrl = response.request().url().url();
        String url = respUrl.getHost() + respUrl.getPath();

        RequestBody body = makeApprovalRequestBody(document);
        Request request = makeApprovalRequest(url, body);

        Call call = mClient.newCall(request);
        try {
            Response execute = call.execute();
            if (!execute.isSuccessful()) {
                throw new WorkerException("Unsuccessful response. Code: " + response.code(), Result.RETRY);
            } else {
                Timber.d("Successfully Approved to Enter the portal");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            throw new WorkerException(e.getMessage(), Result.RETRY);
        }
    }

    class WorkerException extends Exception {
        public Result result;

        WorkerException(String message, Result result) {
            super(message);
            this.result = result;
        }
    }
}
