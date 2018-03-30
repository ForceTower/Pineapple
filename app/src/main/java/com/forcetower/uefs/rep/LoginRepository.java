package com.forcetower.uefs.rep;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.dao.AccessDao;
import com.forcetower.uefs.db.dao.CalendarItemDao;
import com.forcetower.uefs.db.dao.DisciplineClassLocationDao;
import com.forcetower.uefs.db.dao.DisciplineDao;
import com.forcetower.uefs.db.dao.DisciplineGroupDao;
import com.forcetower.uefs.db.dao.GradeDao;
import com.forcetower.uefs.db.dao.GradeInfoDao;
import com.forcetower.uefs.db.dao.GradeSectionDao;
import com.forcetower.uefs.db.dao.MessageDao;
import com.forcetower.uefs.db.dao.ProfileDao;
import com.forcetower.uefs.db.dao.SemesterDao;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.CalendarItem;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.db.entity.Grade;
import com.forcetower.uefs.db.entity.GradeInfo;
import com.forcetower.uefs.db.entity.GradeSection;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db.entity.Semester;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.rep.resources.FetchAllDataResource;
import com.forcetower.uefs.rep.resources.FetchGradesResource;
import com.forcetower.uefs.rep.resources.LoginOnlyResource;
import com.forcetower.uefs.sgrs.SagresResponse;
import com.forcetower.uefs.sgrs.parsers.SagresCalendarParser;
import com.forcetower.uefs.sgrs.parsers.SagresDcpGroupsParser;
import com.forcetower.uefs.sgrs.parsers.SagresDisciplineParser;
import com.forcetower.uefs.sgrs.parsers.SagresGenericParser;
import com.forcetower.uefs.sgrs.parsers.SagresGradeParser;
import com.forcetower.uefs.sgrs.parsers.SagresMessageParser;
import com.forcetower.uefs.sgrs.parsers.SagresScheduleParser;
import com.forcetower.uefs.sgrs.parsers.SagresSemesterParser;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;

import org.jsoup.nodes.Document;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

import static com.forcetower.uefs.rep.helper.RequestCreator.makeApprovalRequest;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeApprovalRequestBody;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeGradesRequest;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeLoginRequest;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeRequestBody;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeStudentPageRequest;

/**
 * Created by João Paulo on 06/03/2018.
 */
@Singleton
public class LoginRepository {
    private final AppExecutors executors;
    private final AppDatabase database;
    private final OkHttpClient client;
    private final ClearableCookieJar cookieJar;
    private final Context context;

    @Inject
    LoginRepository (AppExecutors executors, AppDatabase database, OkHttpClient client,
                     ClearableCookieJar cookieJar, Context context) {
        this.executors = executors;
        this.database = database;
        this.client = client;
        this.cookieJar = cookieJar;
        this.context = context;
    }

    public LiveData<Resource<Integer>> login(String username, String password) {
        MediatorLiveData<Resource<Integer>> values = new MediatorLiveData<>();
        LiveData<Resource<Integer>> data = createInformationLiveData(username, password);
        values.addSource(data, information -> {
            //noinspection ConstantConditions
            if (information.status == Status.SUCCESS) {
                values.removeSource(data);
                LiveData<Resource<Integer>> gradesLive = createGradesLiveData();
                values.addSource(gradesLive, grades -> {
                    //noinspection ConstantConditions
                    if (grades.status == Status.SUCCESS) {
                        values.removeSource(gradesLive);
                        LiveData<Profile> profData = database.profileDao().getProfile();
                        values.addSource(profData, profile -> {
                            values.removeSource(profData);
                            Calendar now = Calendar.getInstance();

                            if (profile != null) {
                                profile.setLastSync(now.getTimeInMillis());
                                Timber.d("Profile updated with new update time");
                                executors.diskIO().execute(() -> {
                                    database.profileDao().insertProfile(profile);
                                    values.postValue(Resource.success(R.string.completed));
                                });
                            }
                        });

                    } else {
                        values.postValue(grades);
                    }
                });
            } else {
                values.postValue(information);
            }
        });
        return values;
    }

    public LiveData<Resource<Integer>> loginOnly(String username, String password) {
        return new LoginOnlyResource(executors) {
            @Override
            public Call createCall() {
                Timber.d("Creating Call...");
                RequestBody body = makeRequestBody(username, password);
                Request request = makeLoginRequest(body);
                return client.newCall(request);
            }

            @Override
            public Call approvalCall(SagresResponse sgrResponse) {
                Timber.d("Creating Approval Call");

                Response response = sgrResponse.getResponse();
                Document document = sgrResponse.getDocument();
                String url = response.request().url().url().getHost() + response.request().url().url().getPath();

                RequestBody body = makeApprovalRequestBody(document);
                Request request = makeApprovalRequest(url, body);
                return client.newCall(request);
            }
        }.asLiveData();
    }

    private LiveData<Resource<Integer>> createInformationLiveData(String username, String password) {
        return new FetchAllDataResource(executors) {
            @Override
            protected void initialPage(Document document) {
                document.charset(Charset.forName("ISO-8859-1"));

                String name  = SagresGenericParser.getName(document);
                double score = SagresGenericParser.getScore(document);

                Timber.d("Your name is %s and your score is %.1f", name, score);

                ProfileDao profileDao = database.profileDao();
                Profile old = profileDao.getProfileDirect();
                long oldLastSync = 0;
                long oldLastAttempt = 0;
                if (old != null) {
                    oldLastSync = old.getLastSync();
                    oldLastAttempt = old.getLastSyncAttempt();
                }
                Profile profile = new Profile(name, score);
                profile.setLastSync(oldLastSync);
                profile.setLastSyncAttempt(oldLastAttempt);
                profileDao.deleteAllProfiles();
                profileDao.insertProfile(profile);

                AccessDao accessDao = database.accessDao();
                Access access = new Access(username, password);
                Access oldAcc = accessDao.getAccessDirect();
                if (oldAcc != null) {
                    oldAcc.copyFrom(access);
                } else {
                    oldAcc = access;
                }

                accessDao.insertAccess(oldAcc);
            }

            @Override
            public Call createCall() {
                Timber.d("Creating Call...");
                RequestBody body = makeRequestBody(username, password);
                Request request = makeLoginRequest(body);
                return client.newCall(request);
            }

            @Override
            public Call approvalCall(SagresResponse sgrResponse) {
                Timber.d("Creating Approval Call");

                Response response = sgrResponse.getResponse();
                Document document = sgrResponse.getDocument();
                String url = response.request().url().url().getHost() + response.request().url().url().getPath();

                RequestBody body = makeApprovalRequestBody(document);
                Request request = makeApprovalRequest(url, body);
                return client.newCall(request);
            }

            @Override
            public Call createStudentPageCall() {
                Timber.d("Creating Student Page Call");
                Request request = makeStudentPageRequest();
                return client.newCall(request);
            }

            @Override
            public void saveResult(@NonNull Document document) {
                Timber.d("Processing document");
                document.charset(Charset.forName("ISO-8859-1"));
                defineMessages(SagresMessageParser.getMessages(document));
                defineCalendar(SagresCalendarParser.getCalendar(document));

                if (defineSemester(SagresSemesterParser.getSemesters(document))) {
                    if (defineDisciplines(SagresDisciplineParser.getDisciplines(document))) {
                        defineDcpGroups(SagresDcpGroupsParser.getGroups(document));
                        try {
                            defineSchedule(SagresScheduleParser.getSchedule(document));
                        } catch (NullPointerException e) {
                            Timber.d("This person has a problem on parser");
                        }
                    }
                }
            }
        }.asLiveData();
    }

    private LiveData<Resource<Integer>> createGradesLiveData() {
        return new FetchGradesResource(executors) {
            @Override
            public Call createGradesCall() {
                Timber.d("Creating Grades Call...");
                Request request = makeGradesRequest();
                return client.newCall(request);
            }

            @Override
            public void saveResult(@NonNull Document document) {
                Timber.d("Saving result for class");
                document.charset(Charset.forName("ISO-8859-1"));

                String semester = SagresGradeParser.getPageSemester(document);
                if (semester == null) {
                    Timber.d("Unable to parse grades on page");
                    return;
                }

                List<Grade> grades = SagresGradeParser.getGrades(document);
                defineGrades(semester, grades, database);
            }
        }.asLiveData();
    }

    public static void defineGrades(@NonNull String semester, @NonNull List<Grade> grades, AppDatabase database) {
        GradeDao gradeDao = database.gradeDao();
        GradeSectionDao sectionDao = database.gradeSectionDao();
        GradeInfoDao infoDao = database.gradeInfoDao();
        DisciplineDao disciplineDao = database.disciplineDao();
        
        Timber.d("All Disciplines: %s", disciplineDao.getAllDisciplinesDirect());

        for (Grade grade : grades) {
            if (grade.getDisciplineName() == null) {
                Timber.d("It's a shame that this guy has no name... He'll be skipped");
                continue;
            }

            String disciplineName = grade.getDisciplineName();
            int pos = disciplineName.indexOf("-");
            String code = disciplineName.substring(0, pos).trim();
            Timber.d("This is the code %s", code);
            Timber.d("That's the semester %s", semester);
            Discipline discipline = disciplineDao.getDisciplinesBySemesterAndCodeDirect(semester, code);
            if (discipline == null) {
                Timber.d("It's a shame that the discipline code %s was not registered... That sucks", code);
                continue;
            }

            int disciplineId = discipline.getUid();
            grade.setDiscipline(disciplineId);

            Grade currentGrade = gradeDao.getDisciplineGradesDirect(disciplineId);
            if (currentGrade == null) {
                Timber.d("Grade didn't exist, created!");
                gradeDao.insertGrade(grade);
            } else {
                currentGrade.selectiveCopy(grade);
                Timber.d("Grade already existed, selective copied");
                gradeDao.insertGrade(currentGrade);
            }

            List<GradeSection> currentSections = sectionDao.getSectionsFromDisciplineDirect(semester, code);

            for (GradeSection section : grade.getSections()) {
                section.setDiscipline(disciplineId);
                String sectionName = section.getName();

                GradeSection currentSection = null;
                for (GradeSection cSection : currentSections) {
                    if (cSection.getName().equalsIgnoreCase(sectionName)) {
                        currentSection = cSection;
                        break;
                    }
                }
                if (currentSection != null) {
                    currentSection.copySelective(section);
                    sectionDao.insertGradeSection(currentSection);
                    Timber.d("Section already existed. Selective copied");

                    int sectionId = currentSection.getUid();
                    List<GradeInfo> infos  = section.getGrades();
                    List<GradeInfo> cInfos = infoDao.getGradesFromSectionDirect(sectionId);
                    if (cInfos.size() == 0) {
                        Timber.d("All grades were inserted because none existed");
                        for (GradeInfo info : infos) {
                            if (info.hasGrade()) info.setNotified(1);
                            else info.setNotified(2);
                            info.setSection(sectionId);
                            infoDao.insertGradeInfo(info);
                        }
                    } else {
                        List<GradeInfo> marked = new ArrayList<>();
                        for (GradeInfo info : infos) {
                            String infoName = info.getEvaluationName();
                            info.setSection(sectionId);
                            GradeInfo currentInfo = null;

                            int equalNames = 0;
                            for (GradeInfo cInfo : cInfos) {
                                if (cInfo.getEvaluationName().equalsIgnoreCase(infoName)) {
                                    equalNames++;
                                }
                            }

                            if (equalNames < 2) {
                                Timber.d("All fine");
                            } else {
                                Timber.d("There are %d things with same name... cool", equalNames);
                            }

                            GradeInfo verOther = null;
                            for (GradeInfo cInfo : cInfos) {
                                if (cInfo.getEvaluationName().equalsIgnoreCase(infoName)) {
                                    currentInfo = cInfo;
                                    if (equalNames > 1) {
                                        Timber.d("Equal names is greater than 1");
                                        if (cInfo.getDate().equalsIgnoreCase(info.getDate())) {
                                            verOther = cInfo;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            }

                            if (verOther != null) {
                                Timber.d("Parsed by date");
                                currentInfo = verOther;
                            } else {
                                Timber.d("Parsed by default");
                            }

                            if (currentInfo == null) {
                                Timber.d("A new grade is here! %s grade: %s", info.getEvaluationName(), info.getGrade());
                                if (info.hasGrade())
                                    info.setNotified(1);
                                else
                                    info.setNotified(2);
                                infoDao.insertGradeInfo(info);
                            } else {
                                currentInfo.selectiveCopy(info);
                                Timber.d("Info already existed, selective copied");
                                infoDao.insertGradeInfo(currentInfo);
                                marked.add(currentInfo);
                            }
                        }

                        Timber.d("Attempt to detect grades that lost track reference");
                        for (GradeInfo cInfo : cInfos) {
                            String mName = cInfo.getEvaluationName();
                            GradeInfo notFound = null;
                            for (GradeInfo marker : marked) {
                                if (marker.getEvaluationName().equalsIgnoreCase(mName)) {
                                    notFound = cInfo;
                                    break;
                                }
                            }
                            if (notFound == null) {
                                Timber.d("A grade lost reference %s", cInfo.getEvaluationName());
                                cInfo.setLost(true);
                                infoDao.insertGradeInfo(cInfo);
                            }
                        }
                    }
                } else {
                    Long sectionId = sectionDao.insertGradeSection(section);
                    for (GradeInfo info : section.getGrades()) {
                        info.setSection(sectionId.intValue());
                        if (info.hasGrade()) info.setNotified(1);
                        else info.setNotified(2);
                        infoDao.insertGradeInfo(info);
                    }
                    Timber.d("Section created. All grades were inserted");
                }
            }
        }
    }

    private void defineSchedule(List<DisciplineClassLocation> schedule) {
        if (schedule == null) {
            Timber.d("Semester schedule not defined. Skipping");
            return;
        }
        if (schedule.isEmpty()) {
            Timber.d("Semester schedule is empty... Did you leave the university or is the parser broken?");
            return;
        }

        try {
            List<Semester> semesters = database.semesterDao().getAllSemestersDirect();
            String semester = Semester.getCurrentSemester(semesters).getName();

            DisciplineGroupDao groupDao = database.disciplineGroupDao();
            DisciplineClassLocationDao locationDao = database.disciplineClassLocationDao();
            DisciplineDao disciplineDao = database.disciplineDao();

            Timber.d("Previous schedule will be cleared... No selective copy this time");
            locationDao.deleteLocationsFromSemester(semester);

            for (DisciplineClassLocation location : schedule) {
                String code = location.getClassCode();
                List<DisciplineGroup> groups = groupDao.getDisciplineGroupsDirect(semester, code);
                if (groups.isEmpty()) {
                    Timber.d("It's sad how the code %s at semester %s has no groups, wow", code, semester);
                } else {
                    if (groups.size() == 1) {
                        int groupId = groups.get(0).getUid();
                        int disciplineId = groups.get(0).getDiscipline();
                        Discipline discipline = disciplineDao.getDisciplinesByIdDirect(disciplineId);
                        location.setClassName(discipline.getName());
                        location.setGroupId(groupId);
                        locationDao.insertClassLocation(location);
                    } else {
                        DisciplineGroup selected = null;
                        for (DisciplineGroup group : groups) {
                            if (group.getGroup().trim().equalsIgnoreCase(location.getClassGroup())) {
                                selected = group;
                                break;
                            }
                        }
                        if (selected == null) {
                            Timber.d("Might have been assigned to invalid group");
                            selected = groups.get(0);
                            int groupId = groups.get(0).getUid();
                            int disciplineId = groups.get(0).getDiscipline();
                            Discipline discipline = disciplineDao.getDisciplinesByIdDirect(disciplineId);
                            location.setClassName(discipline.getName());
                            location.setGroupId(groupId);
                            locationDao.insertClassLocation(location);
                        } else {
                            Timber.d("Found the partner for discipline! %s to %s", code, selected.getGroup());
                        }

                        int groupId = selected.getUid();
                        int disciplineId = selected.getDiscipline();
                        Discipline discipline = disciplineDao.getDisciplinesByIdDirect(disciplineId);
                        location.setClassName(discipline.getName());
                        location.setGroupId(groupId);
                        locationDao.insertClassLocation(location);

                    }
                }
            }
        } catch (Exception e) {
            Timber.d("No error should move pass this guy for now");
            Timber.e(e.getMessage());
            Timber.e("This is the error");
        }
    }

    private void defineDcpGroups(List<DisciplineGroup> groups) {
        if (groups.size() == 0) {
            Timber.d("No discipline groups found");
            return;
        }

        DisciplineDao disciplineDao = database.disciplineDao();
        DisciplineGroupDao groupDao = database.disciplineGroupDao();

        for (DisciplineGroup group : groups) {
            String code = group.getCode();
            String semester = group.getSemester();

            Discipline discipline = disciplineDao.getDisciplinesBySemesterAndCodeDirect(semester, code);
            group.setDiscipline(discipline.getUid());

            List<DisciplineGroup> currentGrp = groupDao.getDisciplineGroupsDirect(discipline.getUid());

            if (currentGrp.size() == 0) {
                Timber.d("No disciplines for this guy, just insert");
                Timber.d("Group inserted");
                groupDao.insertDisciplineGroup(group);
            } else {
                String upGroup = group.getGroup();
                if (upGroup == null) {
                    Timber.d("Up group is null");
                    Timber.d("This means there should be only one in list if any");
                    if (currentGrp.size() == 1) {
                        Timber.d("I was right");
                        DisciplineGroup oldGroup = currentGrp.get(0);
                        if (oldGroup.isDraft()) {
                            group.setUid(oldGroup.getUid());
                            groupDao.insertDisciplineGroup(group);
                        }
                    } else {
                        Timber.d("This was ignored");
                        Timber.d("Group is null and there more than one");
                    }
                } else {
                    boolean found = false;
                    for (DisciplineGroup currentGroup : currentGrp) {
                        if (currentGroup.getGroup().trim().equalsIgnoreCase(upGroup.trim())) {
                            if (currentGroup.isDraft()) {
                                group.setUid(currentGroup.getUid());
                                groupDao.insertDisciplineGroup(group);
                            }
                            found = true;
                            break;
                        }
                    }

                    if(!found) {
                        groupDao.insertDisciplineGroup(group);
                        Timber.d("Group inserted.. It's a new one");
                    }
                }
            }
        }

    }

    private boolean defineDisciplines(@NonNull List<Discipline> disciplines) {
        if (disciplines.size() == 0) {
            Timber.d("No disciplines found");
            return false;
        }

        DisciplineDao disciplineDao = database.disciplineDao();
        for (Discipline discipline : disciplines) {
            String code = discipline.getCode();
            String semester = discipline.getSemester();

            Discipline current = disciplineDao.getDisciplinesBySemesterAndCodeDirect(semester, code);
            if (current == null) {
                disciplineDao.insertDiscipline(discipline);
                Timber.d("It's a new discipline. Wow!");
            } else {
                discipline.setUid(current.getUid());
                disciplineDao.insertDiscipline(discipline);
            }
        }
        Timber.d("Disciplines were updated");
        return true;
    }

    private boolean defineSemester(@NonNull List<Semester> semesters) {
        if (semesters.size() == 0) {
            Timber.d("Semesters not found... Are you a freshman?");
            return false;
        }

        SemesterDao semesterDao = database.semesterDao();
        for (Semester semester : semesters) {
            Semester val = semesterDao.getSemesterByNameDirect(semester.getName());
            if (val == null) {
                semesterDao.insertSemesters(semester);
                Timber.d("It's a new semester!");
            }
        }

        Timber.d("Semesters were updated");
        return true;
    }

    private void defineCalendar(@Nullable List<CalendarItem> calendarItems) {
        if (calendarItems == null) {
            Timber.d("Calendar was not parsed");
            return;
        }

        CalendarItemDao calendarItemDao = database.calendarItemDao();
        if (calendarItems.size() > 0) calendarItemDao.deleteCalendar();

        for (CalendarItem calendarItem : calendarItems) {
            calendarItemDao.insertItems(calendarItem);
        }

        Timber.d("Calendar was updated");
    }

    private void defineMessages(@NonNull List<Message> messages) {
        MessageDao messageDao = database.messageDao();
        for (Message message : messages) {
            List<Message> alike = messageDao.getMessagesDirectLike(message.getMessage(), message.getSender());
            if (alike.isEmpty()) {
                Timber.d("New message detected");
                messageDao.insertMessages(message);
            } else {
                Timber.d("Alike messages amount: %d", alike.size());
                Timber.d("This is not a new message");
                alike.get(0).selectiveCopy(message);
                messageDao.insertMessages(alike.get(0));
            }
        }
        Timber.d("Messages were merged");
    }

    public LiveData<Resource<Integer>> logout() {
        Timber.d("Logout requested");
        cookieJar.clear();
        MutableLiveData<Resource<Integer>> logout = new MutableLiveData<>();
        executors.diskIO().execute(() -> {
            deleteDatabase();
            Timber.d("%s", database.gradeInfoDao().getGradesFromSectionDirect(1));
            File downloadedFile = new File(context.getCacheDir(), Constants.ENROLLMENT_CERTIFICATE_FILE_NAME);
            if (downloadedFile.exists()) downloadedFile.delete();
            logout.postValue(Resource.success(R.string.data_deleted));
        });

        return logout;
    }

    @WorkerThread
    public void deleteDatabase() {
        database.accessDao().deleteAllAccesses();
        database.todoItemDao().deleteAllTodoItems();
        database.messageDao().deleteAllMessages();
        database.profileDao().deleteAllProfiles();
        database.calendarItemDao().deleteCalendar();
        database.gradeInfoDao().deleteAllGradesInfo();
        database.disciplineClassLocationDao().deleteAllDisciplineClassLocations();
        database.disciplineClassItemDao().deleteAllDisciplineClassItems();
        database.disciplineGroupDao().deleteAllDisciplineGroups();
        database.gradeSectionDao().deleteAllGradeSections();
        database.gradeDao().deleteAllGrades();
        database.disciplineDao().deleteAllDisciplines();
        database.semesterDao().removeAllSemesters();
    }

    public void deleteAccess() {
        executors.diskIO().execute(() -> database.accessDao().deleteAllAccesses());
    }

    public void deleteAllMessagesNotifications() {
        executors.diskIO().execute(() -> database.messageDao().clearAllNotifications());
    }

    public void deleteAllGradesNotifications() {
        executors.diskIO().execute(() -> database.gradeInfoDao().clearAllNotifications());
    }
}
