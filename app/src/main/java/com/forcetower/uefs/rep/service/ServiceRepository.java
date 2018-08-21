package com.forcetower.uefs.rep.service;

import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.db_service.entity.AboutField;
import com.forcetower.uefs.db_service.entity.Course;
import com.forcetower.uefs.db_service.entity.Mention;
import com.forcetower.uefs.db_service.entity.QuestionAnswer;
import com.forcetower.uefs.db_service.entity.Version;
import com.forcetower.uefs.db_service.helper.CreditAndMentions;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.resources.NetworkBoundResource;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.UNEService;
import com.forcetower.uefs.service.UserElevation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by João Paulo on 01/04/2018.
 */
@Singleton
public class ServiceRepository {
    private final UNEService service;
    private final AppExecutors executors;
    private final ServiceDatabase database;

    @Inject
    public ServiceRepository(UNEService service, AppExecutors executors, ServiceDatabase database) {
        this.service = service;
        this.executors = executors;
        this.database = database;
    }

    public LiveData<ApiResponse<Version>> getUNESVersion() {
        return service.getLatestVersion();
    }

    public LiveData<Resource<List<CreditAndMentions>>> getCredits() {
        return new NetworkBoundResource<List<CreditAndMentions>, List<CreditAndMentions>>(executors) {

            @Override
            protected void saveCallResult(@NonNull List<CreditAndMentions> items) {
                database.creditsMentionDao().deleteAll();
                for (CreditAndMentions item : items) {
                    for (Mention mention : item.getParticipants()) {
                        mention.setCreditId(item.getUid());
                    }
                    database.creditsMentionDao().insertFull(item, item.getParticipants());
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<CreditAndMentions> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<CreditAndMentions>> loadFromDb() {
                return database.creditsMentionDao().getCreditsWithMentions();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<CreditAndMentions>>> createCall() {
                return service.getCredits();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<QuestionAnswer>>> getFAQ() {
        return new NetworkBoundResource<List<QuestionAnswer>, List<QuestionAnswer>>(executors) {

            @Override
            protected void saveCallResult(@NonNull List<QuestionAnswer> item) {
                database.questionAnswerDao().deleteAllQuestions();
                database.questionAnswerDao().insert(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<QuestionAnswer> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<QuestionAnswer>> loadFromDb() {
                return database.questionAnswerDao().getFAQ();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<QuestionAnswer>>> createCall() {
                return service.getFAQ();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<AboutField>>> getAbout() {
        return new NetworkBoundResource<List<AboutField>, List<AboutField>>(executors) {
            @Override
            protected void saveCallResult(@NonNull List<AboutField> item) {
                database.aboutFieldDao().deleteAllAbout();
                database.aboutFieldDao().insert(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<AboutField> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<AboutField>> loadFromDb() {
                return database.aboutFieldDao().getAbout();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<AboutField>>> createCall() {
                return service.getAbout();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Course>>> getCourses() {
        return new NetworkBoundResource<List<Course>, List<Course>>(executors) {
            @Override
            protected void saveCallResult(@NonNull List<Course> item) {
                database.courseDao().deleteAndInsertAll(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Course> data) {
                return data == null || data.isEmpty() || data.get(0).isOutdated();
            }

            @NonNull
            @Override
            protected LiveData<List<Course>> loadFromDb() {
                return database.courseDao().getAllCourses();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Course>>> createCall() {
                return service.getCourses();
            }
        }.asLiveData();
    }

    public LiveData<ApiResponse<UserElevation>> getUserElevation(String username) {
        return service.getUserElevation(username);
    }
}
