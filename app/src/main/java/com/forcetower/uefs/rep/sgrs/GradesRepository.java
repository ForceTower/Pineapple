package com.forcetower.uefs.rep.sgrs;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.dao.DisciplineDao;
import com.forcetower.uefs.db.dao.GradeDao;
import com.forcetower.uefs.db.dao.GradeInfoDao;
import com.forcetower.uefs.db.dao.GradeSectionDao;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.DisciplineMissedClass;
import com.forcetower.uefs.db.entity.Grade;
import com.forcetower.uefs.db.entity.GradeInfo;
import com.forcetower.uefs.db.entity.GradeSection;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.resources.FetchAllGradesResource;
import com.forcetower.uefs.sgrs.parsers.SagresGradeParser;
import com.forcetower.uefs.sgrs.parsers.SagresMissedClassesParser;

import org.jsoup.nodes.Document;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import timber.log.Timber;

import static com.forcetower.uefs.rep.helper.RequestCreator.makeGradesRequest;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeListGradeRequests;
import static com.forcetower.uefs.rep.sgrs.LoginRepository.defineGrades;
import static com.forcetower.uefs.rep.sgrs.LoginRepository.defineMissedClasses;
import static com.forcetower.uefs.rep.sgrs.LoginRepository.redefinePages;

/**
 * Created by João Paulo on 07/03/2018.
 */
@Singleton
public class GradesRepository {
    private final AppDatabase database;
    private final GradeSectionDao sectionDao;
    private final DisciplineDao disciplineDao;
    private final GradeInfoDao infoDao;
    private final GradeDao gradeDao;
    private final AppExecutors executors;
    private final OkHttpClient client;

    @Inject
    GradesRepository(AppDatabase database, AppExecutors executors, OkHttpClient client) {
        this.database = database;
        this.sectionDao = database.gradeSectionDao();
        this.disciplineDao = database.disciplineDao();
        this.infoDao = database.gradeInfoDao();
        this.executors = executors;
        this.gradeDao = database.gradeDao();
        this.client = client;
    }

    public LiveData<List<Discipline>> getGrades(String semester) {
        MediatorLiveData<List<Discipline>> result = new MediatorLiveData<>();
        //We start by getting all disciplines
        LiveData<List<Discipline>> disciplineSrc = disciplineDao.getDisciplinesFromSemester(semester);
        result.addSource(disciplineSrc, disciplines -> {
            result.removeSource(disciplineSrc);
            //When receive all disciplines, we go to each of them to retrieve data
            //noinspection ConstantConditions
            executors.diskIO().execute(() -> {
                //noinspection ConstantConditions
                for (Discipline discipline : disciplines) {
                    List<GradeSection> sections = sectionDao.getSectionsFromDisciplineDirect(discipline.getUid());
                    //Then, go to each section
                    //noinspection ConstantConditions
                    for (GradeSection section : sections) {
                        List<GradeInfo> infos = infoDao.getGradesFromSectionDirect(section.getUid());
                        //add all the infos
                        section.setGradeInfos(infos);
                    }
                    Grade grade = gradeDao.getDisciplineGradesDirect(discipline.getCode(), semester);
                    discipline.setSections(sections);
                    discipline.setGrade(grade);
                }

                result.postValue(disciplines);
            });
        });

        return result;
    }

    public LiveData<Discipline> getGradesFromDiscipline(int disciplineId) {
        MediatorLiveData<Discipline> result = new MediatorLiveData<>();
        //We start by getting all disciplines
        LiveData<Discipline> disciplineSrc = disciplineDao.getDisciplineById(disciplineId);
        result.addSource(disciplineSrc, discipline -> {
            result.removeSource(disciplineSrc);
            //When receive all disciplines, we go to each of them to retrieve data
            //noinspection ConstantConditions
            executors.diskIO().execute(() -> {
                //noinspection ConstantConditions
                    List<GradeSection> sections = sectionDao.getSectionsFromDisciplineDirect(discipline.getUid());
                    //Then, go to each section
                    //noinspection ConstantConditions
                    for (GradeSection section : sections) {
                        List<GradeInfo> infos = infoDao.getGradesFromSectionDirect(section.getUid());
                        //add all the infos
                        section.setGradeInfos(infos);
                    }
                    Grade grade = gradeDao.getDisciplineGradesDirect(discipline.getCode(), discipline.getSemester());
                    discipline.setSections(sections);
                    discipline.setGrade(grade);

                result.postValue(discipline);
            });
        });

        return result;
    }

    public LiveData<Resource<Integer>> getAllGrades() {
        return new FetchAllGradesResource(executors) {
            @NonNull
            @Override
            protected List<Call> createCallsFromDocument(@NonNull Document document) {
                Timber.d("Creating all calls needed");
                List<Request> requests = makeListGradeRequests(document);
                List<Call> calls = new ArrayList<>();

                for (Request request : requests) {
                    Call call = client.newCall(request);
                    calls.add(call);
                }

                Timber.d("Number of calls created: %d", calls.size());
                return calls;
            }

            @Override
            public void saveResult(@NonNull Document document) {
                Timber.d("Saving result for grades");
                document.charset(Charset.forName("ISO-8859-1"));

                String semester = SagresGradeParser.getPageSemester(document);
                if (semester == null) {
                    Crashlytics.log("Unable to find the semester...");
                    Timber.d("Unable to parse grades on page");
                    return;
                }
                Timber.d("Semester is: %s. Good luck!", semester);
                List<Grade> grades = SagresGradeParser.getGrades(document);
                Pair<Boolean, List<DisciplineMissedClass>> missedClasses = SagresMissedClassesParser.getMissedClasses(document);
                redefinePages(semester, grades, database);
                defineGrades(semester, grades, database);
                if (!missedClasses.first) {
                    defineMissedClasses(semester, missedClasses.second, database);
                } else {
                    Timber.d("Missed classes error");
                }
            }

            @NonNull
            @Override
            protected Call createFirstGradesCall() {
                Request request = makeGradesRequest();
                return client.newCall(request);
            }
        }.asLiveData();
    }

    public void clearAllNotifications() {
        executors.diskIO().execute(infoDao::clearAllNotifications);
    }

    public LiveData<List<Discipline>> requestAllGrades() {
        MediatorLiveData<List<Discipline>> mediator = new MediatorLiveData<>();
        LiveData<List<Grade>> allGrades = gradeDao.getAllGrades();

        mediator.addSource(allGrades, grades -> {
            mediator.removeSource(allGrades);
            executors.diskIO().execute(() -> {
                List<Discipline> disciplines = new ArrayList<>();
                //noinspection ConstantConditions
                for (Grade grade : grades) {
                    Discipline discipline = disciplineDao.getDisciplinesByIdDirect(grade.getDiscipline());
                    discipline.setGrade(grade);
                    disciplines.add(discipline);
                }

                mediator.postValue(disciplines);
            });
        });

        return mediator;
    }
}
