package com.forcetower.uefs.services.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.forcetower.uefs.database.entities.AAccess;
import com.forcetower.uefs.database.entities.ACalendarItem;
import com.forcetower.uefs.database.entities.ADiscipline;
import com.forcetower.uefs.database.entities.ADisciplineClassItem;
import com.forcetower.uefs.database.entities.ADisciplineClassLocation;
import com.forcetower.uefs.database.entities.ADisciplineGroup;
import com.forcetower.uefs.database.entities.AGradeInfo;
import com.forcetower.uefs.database.entities.AGradeSection;
import com.forcetower.uefs.database.entities.AScrap;
import com.forcetower.uefs.database.entities.ASemester;
import com.forcetower.uefs.database.repository.AccessRepository;
import com.forcetower.uefs.database.repository.CalendarRepository;
import com.forcetower.uefs.database.repository.DisciplineClassItemRepository;
import com.forcetower.uefs.database.repository.DisciplineClassLocationRepository;
import com.forcetower.uefs.database.repository.DisciplineGroupRepository;
import com.forcetower.uefs.database.repository.DisciplineRepository;
import com.forcetower.uefs.database.repository.GradeInfoRepository;
import com.forcetower.uefs.database.repository.GradeSectionRepository;
import com.forcetower.uefs.database.repository.ScrapRepository;
import com.forcetower.uefs.database.repository.SemesterRepository;
import com.forcetower.uefs.dependency_injection.component.ApplicationComponent;
import com.forcetower.uefs.sagres_sdk.domain.GradeInfo;
import com.forcetower.uefs.sagres_sdk.domain.GradeSection;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.domain.SagresCalendarItem;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDetails;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassGroup;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassItem;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassTime;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.domain.SagresSemester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import static com.forcetower.uefs.Constants.APP_TAG;
import static com.forcetower.uefs.helpers.Utils.parseIntOrZero;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class MigrateToLocalDatabaseTask extends AsyncTask<Void, Void, Void> {
    @Inject
    AccessRepository accessRepository;
    @Inject
    DisciplineRepository disciplineRepository;
    @Inject
    DisciplineGroupRepository disciplineGroupRepository;
    @Inject
    DisciplineClassItemRepository disciplineClassItemRepository;
    @Inject
    DisciplineClassLocationRepository disciplineClassLocationRepository;
    @Inject
    ScrapRepository scrapRepository;
    @Inject
    CalendarRepository calendarRepository;
    @Inject
    SemesterRepository semesterRepository;
    @Inject
    GradeSectionRepository gradeSectionRepository;
    @Inject
    GradeInfoRepository gradeInfoRepository;

    public MigrateToLocalDatabaseTask(ApplicationComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            accessRepository.deleteAllAccesses();
            disciplineRepository.deleteAllDisciplines();
            disciplineGroupRepository.deleteAllDisciplineGroups();
            disciplineClassItemRepository.deleteAllDisciplineClassItems();
            disciplineClassLocationRepository.deleteAllDisciplineClassLocations();
            scrapRepository.deleteAllScraps();
            calendarRepository.deleteCalendar();
            semesterRepository.removeAllSemesters();
            gradeSectionRepository.deleteAllGradeSections();
            gradeInfoRepository.deleteAllGradesInfo();

            SagresAccess access = SagresAccess.getCurrentAccess();
            accessRepository.insertAccess(new AAccess(access.getUsername(), access.getPassword()));

            SagresProfile profile = SagresProfile.getCurrentProfile();

            List<SagresCalendarItem> calendar = profile.getCalendar();
            if (calendar != null && calendar.size() > 0) {
                ACalendarItem[] items = new ACalendarItem[calendar.size()];
                transformCalendar(calendar).toArray(items);
                calendarRepository.insertItems(items);
                Log.d(APP_TAG, "Inserted calendar: " + Arrays.toString(items));
            }
            Log.d(APP_TAG, "All calendar: " + calendarRepository.getCalendar());

            List<SagresMessage> messages = profile.getMessages();
            if (messages != null && messages.size() > 0) {
                AScrap[] items = new AScrap[messages.size()];
                transformScraps(messages).toArray(items);
                scrapRepository.insertScraps(items);
                Log.d(APP_TAG, "Inserted messages: " + Arrays.toString(items));
            }
            Log.d(APP_TAG, "All messages: " + scrapRepository.getAllScraps());

            handleDisciplines();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void handleDisciplines() {
        SagresProfile profile = SagresProfile.getCurrentProfile();
        if (profile.getAllSemestersGrades() != null) {
            Set<SagresSemester> semesterSet = profile.getAllSemestersGrades().keySet();
            if (semesterSet.size() > 0) {
                ASemester[] items = new ASemester[semesterSet.size()];
                transformSemester(semesterSet).toArray(items);
                semesterRepository.insertSemesters(items);
                Log.d(APP_TAG, "Inserted semesters: " + Arrays.toString(items));
            }
        }
        Log.d(APP_TAG, "All semesters: " + semesterRepository.getAllSemesters());

        List<SagresClassDetails> classesDetails = profile.getClassesDetails();
        if (classesDetails != null && classesDetails.size() > 0) {
            ADiscipline[] disciplines = new ADiscipline[classesDetails.size()];
            transformDiscipline(classesDetails).toArray(disciplines);
            disciplineRepository.insertDiscipline(disciplines);
            Log.d(APP_TAG, "Inserted disciplines: " + Arrays.toString(disciplines));

            for (SagresClassDetails details : classesDetails) {
                ADiscipline discipline = disciplineRepository.getDisciplinesBySemesterAndCode(details.getSemester(), details.getCode());
                int disciplineUid = discipline.getUid();

                List<SagresClassGroup> groups = details.getGroups();
                if (groups != null && !groups.isEmpty()) {
                    for (SagresClassGroup group : groups) {
                        ADisciplineGroup disciplineGroup = new ADisciplineGroup(disciplineUid, group.getTeacher(), group.getType(),
                                parseIntOrZero(group.getCredits()), parseIntOrZero(group.getMissLimit()),
                                group.getClassPeriod(), group.getDepartment());
                        disciplineGroup.setDraft(group.isDraft());

                        Long groupId = disciplineGroupRepository.insertDisciplineGroup(disciplineGroup);
                        Log.i(APP_TAG, "Inserted Group: " + disciplineGroup.getGroup() + " Teacher: " + disciplineGroup.getTeacher() +" id::" + groupId.intValue());

                        if (!group.isDraft()) {
                            List<SagresClassTime> classesTimes = group.getClassTimeList();
                            List<SagresClassItem> classItems = group.getClasses();

                            for (SagresClassTime time : classesTimes) {
                                ADisciplineClassLocation classLocation = new ADisciplineClassLocation(groupId.intValue(), time.getStart(), time.getFinish(), time.getDay(), null, null, null);
                                disciplineClassLocationRepository.insertClassLocation(classLocation);
                            }

                            for (SagresClassItem item : classItems) {
                                ADisciplineClassItem classItem = new ADisciplineClassItem(groupId.intValue(), parseIntOrZero(item.getNumber()), item.getSituation(), item.getSubject(), item.getDate(), parseIntOrZero(item.getNumberOfMaterials()));
                                disciplineClassItemRepository.insertClassItem(classItem);
                            }
                        }

                    }
                }

                HashMap<String, SagresGrade> actualSemesterGrades = profile.getGrades();

                SagresGrade grades = profile.getGradesOfClass(details.getCode(), details.getSemester());
                if (grades != null) {
                    List<GradeSection> sections = grades.getSections();
                    if (sections != null) {
                        for (GradeSection section : sections) {
                            AGradeSection created = new AGradeSection(disciplineUid, section.getName());
                            created.setPartialMean(section.getPartialMean());
                            Long sectionUid = gradeSectionRepository.insertGradeSection(created);

                            List<GradeInfo> gradesInfo = section.getGrades();
                            if (gradesInfo != null) {
                                for (GradeInfo gradeInfo : gradesInfo) {
                                    AGradeInfo info = new AGradeInfo(sectionUid.intValue(), gradeInfo.getEvaluationName(), gradeInfo.getGrade(), gradeInfo.getDate());
                                    gradeInfoRepository.insertGradeInfo(info);
                                }
                            }
                        }
                    }
                }
            }
        }
        Log.d(APP_TAG, "All disciplines: " + disciplineRepository.getAllDisciplines());
        Log.d(APP_TAG, "All grades info: " + gradeInfoRepository.getAllGradeInfos());
        Log.d(APP_TAG, "Disciplines in 2k17.2 " + disciplineClassLocationRepository.getClassesFromSemester("20172"));
    }

    private List<ADiscipline> transformDiscipline(List<SagresClassDetails> before) {
        List<ADiscipline> after = new ArrayList<>();
        if (before == null) {
            return after;
        }

        for (SagresClassDetails old : before) {
            ADiscipline created = new ADiscipline(old.getSemester(), old.getName(), old.getCode());
            created.setLastClass(old.getLastClass());
            created.setNextClass(old.getNextClass());
            created.setCredits(parseIntOrZero(old.getCredits()));
            created.setMissedClasses(parseIntOrZero(old.getMissedClasses()));
            created.setMissedClassesInformed(parseIntOrZero(old.getMissedClassesInformed()));
            after.add(created);
        }

        return after;
    }

    private List<ASemester> transformSemester(Set<SagresSemester> before) {
        List<ASemester> after = new ArrayList<>();
        if (before == null) {
            return after;
        }

        for (SagresSemester old : before) {
            after.add(new ASemester(old.getSemesterCode(), old.getName()));
        }

        return after;
    }

    private List<AScrap> transformScraps(List<SagresMessage> before) {
        List<AScrap> after = new ArrayList<>();
        if (before == null) {
            return after;
        }
        for (SagresMessage old : before) {
            after.add(new AScrap(old.getSender(), old.getMessage(), old.getReceivedTime(), old.getClassName()));
        }

        return after;
    }


    public static List<ACalendarItem> transformCalendar(List<SagresCalendarItem> before) {
        List<ACalendarItem> after = new ArrayList<>();
        if (before == null) {
            return after;
        }

        for (SagresCalendarItem old : before) {
            if (old.getDay() != null || old.getMessage() != null)
                after.add(new ACalendarItem(old.getDay(), old.getMessage()));
        }

        return after;
    }
}
