package com.forcetower.uefs.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.db.entity.Grade;
import com.forcetower.uefs.db.entity.GradeInfo;
import com.forcetower.uefs.db.entity.GradeSection;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db.entity.Semester;
import com.forcetower.uefs.util.ValueUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;

import static com.forcetower.uefs.util.DateUtils.generateCalendar;
import static com.forcetower.uefs.util.DateUtils.getDateDiff;

/**
 * Created by João Paulo on 10/04/2018.
 */
public class AchievementsViewModel extends ViewModel {
    private final AppDatabase database;
    private final AppExecutors executors;
    private MediatorLiveData<HashMap<Integer, Integer>> checkAch;

    @Inject
    public AchievementsViewModel(AppDatabase database, AppExecutors executors) {
        this.database = database;
        this.executors = executors;
    }

    public LiveData<HashMap<Integer, Integer>> checkAchievements() {
        checkAch = new MediatorLiveData<>();
        HashMap<Integer, Integer> unlocked = new HashMap<>();
        //HashMap<String, Integer> kickOutGame = new HashMap<>();

        LiveData<List<GradeInfo>> infosSrc = database.gradeInfoDao().getAllGradeInfos();
        checkAch.addSource(infosSrc, infos -> {
            checkAch.removeSource(infosSrc);
            //Unlock single grade based achievements
            unlockForSingleGrades(infos, unlocked);

            LiveData<List<Semester>> semestersSrc = database.semesterDao().getAllSemesters();
            checkAch.addSource(semestersSrc, semesters -> {
                checkAch.removeSource(semestersSrc);
                executors.diskIO().execute(() -> {
                    if (semesters == null) return;

                    unlockSemesterBased(semesters, unlocked);

                    Profile profile = database.profileDao().getProfileDirect();
                    if (profile != null && profile.getScore() >= 7 && semesters.size() > 5)
                        unlocked.put(R.string.achievement_survivor, -1);

                    for (Semester semester : semesters) {
                        String smt = semester.getName();

                        List<Discipline> disciplines = database.disciplineDao().getDisciplinesFromSemesterDirect(smt);
                        unlockForDisciplineSemester(disciplines, unlocked/*, kickOutGame*/);
                    }

                    List<Discipline> disciplines = database.disciplineDao().getAllDisciplinesDirect();
                    unlockForAllDisciplines(disciplines, unlocked);

                    String currentSmt = Semester.getCurrentSemester(semesters).getName();
                    LiveData<List<DisciplineClassLocation>> locationsSrc = database.disciplineClassLocationDao().getClassesFromSemester(currentSmt);
                    executors.mainThread().execute(() -> checkAch.addSource(locationsSrc, locations -> {
                        checkAch.removeSource(locationsSrc);
                        executors.diskIO().execute(() -> {
                            unlockForLocations(locations, unlocked);
                            checkAch.postValue(unlocked);
                        });
                    }));
                });
            });
        });

        return checkAch;
    }

    private void unlockForAllDisciplines(List<Discipline> disciplines, HashMap<Integer, Integer> unlocked) {
        int count = 0;

        for (Discipline discipline : disciplines) {
            String name = discipline.getName();
            if (name == null) continue;

            if (name.matches("(?i)(.*)introdu([cç])([aã])o(.*)")) {
                count++;
            } else if (name.matches("(?i)(.*)int(r)?\\.(.*)")) {
                count++;
            }
        }

        unlocked.put(R.string.achievement_introduction_to_introductions, count);
    }

    @WorkerThread
    private void unlockForLocations(@Nullable List<DisciplineClassLocation> locations, @NonNull HashMap<Integer, Integer> unlocked) {
        if (locations == null) return;
        boolean mod1 = false;
        boolean mod7 = false;

        Hashtable<String, List<DisciplineClassLocation>> mapping = new Hashtable<>();
        for (DisciplineClassLocation location : locations) {
            String mod = location.getModulo();
            if (mod != null) {
                if (!mod1) mod1 = (mod.equalsIgnoreCase("Módulo 1") || mod.equalsIgnoreCase("Modulo 1"));
                if (!mod7) mod7 = (mod.equalsIgnoreCase("Módulo 7") || mod.equalsIgnoreCase("Modulo 7"));
            }

            String day = location.getDay();

            List<DisciplineClassLocation> classes = mapping.get(day);
            if (classes == null) classes = new ArrayList<>();

            classes.add(location);
            mapping.put(day, classes);
        }

        if (mod1 && mod7) unlocked.put(R.string.achievement_dora_the_explorer, -1);

        for (String day : mapping.keySet()) {
            int minutes = 0;
            List<DisciplineClassLocation> locals = mapping.get(day);
            for (DisciplineClassLocation local : locals) {
                Calendar start = generateCalendar(local.getStartTime());
                Calendar end   = generateCalendar(local.getEndTime());
                if (start != null && end != null) {
                    long difference = getDateDiff(start.getTime(), end.getTime(), TimeUnit.MINUTES);
                    minutes += difference;
                }
            }

            if (minutes >= 480) {
                unlocked.put(R.string.achievement_marathonist, -1);
                break;
            }
        }
    }

    @WorkerThread
    private void unlockForDisciplineSemester(@Nullable List<Discipline> disciplines, @NonNull HashMap<Integer, Integer> unlocked/*, @NonNull HashMap<String, Integer> kickOutGame*/) {
        if (disciplines == null) return;

        boolean allDisciplinesApproved = true;
        boolean allDisciplinesMechanic = disciplines.size() > 0;
        int semesterHours = 0;

        for (Discipline discipline : disciplines) {
            semesterHours += discipline.getCredits();

            if (discipline.getMissedClasses() == 0)
                unlocked.put(R.string.achievement_always_there, -1);

            if (discipline.getSituation() != null && (discipline.getSituation().equalsIgnoreCase("Reprovado por Falta")
                    || discipline.getCredits()/4 >= discipline.getMissedClasses())) {
                unlocked.put(R.string.achievement_i_have_never_seen, -1);
            }

            /*
            if (discipline.getName().matches("(?i)(.*)aprovado(.*)")) {
                kickOutGame.put(discipline.getCode(), -1);
            } else {
                Integer val = kickOutGame.get(discipline.getCode());
                if (val == null) kickOutGame.put(discipline.getCode(), 1);
                else if (val >= 0) kickOutGame.put(discipline.getCode(), val + 1);
            }
            */

            if (discipline.getSituation() != null && !discipline.getSituation().equalsIgnoreCase("Aprovado")) {
                allDisciplinesApproved = false;
            }

            Grade grade = database.gradeDao().getDisciplineGradesDirect(discipline.getUid());
            if (grade != null && grade.getFinalScore() != null) {
                String finalScore = grade.getFinalScore();
                if (finalScore.equalsIgnoreCase("10,0")) unlocked.put(R.string.achievement_10_mean, -1);

                double val = ValueUtils.toDouble(finalScore.replace(",", "."), -1);
                if (val >= 5 && val < 7) unlocked.put(R.string.achievement_fight_till_the_end, -1);
                if (val == 5) unlocked.put(R.string.achievement_almost, -1);
                if (val >= 9.5 && val < 10) unlocked.put(R.string.achievement_so_close_yet_so_far, -1);
                if (val < 8) allDisciplinesMechanic = false;
            }

            List<GradeSection> sections = database.gradeSectionDao().getSectionsFromDisciplineDirect(discipline.getUid());
            List<GradeInfo> infos;
            if (sections.size() > 0) {
                if (sections.size() > 1) {
                    infos = moreThanOne(sections);
                } else {
                    infos = database.gradeInfoDao().getGradesFromSectionDirect(sections.get(0).getUid());
                }

                if (infos.size() == 3) {
                    GradeInfo p1 = infos.get(0);
                    GradeInfo p2 = infos.get(1);
                    GradeInfo p3 = infos.get(2);

                    if (p1.hasGrade() && p2.hasGrade() && p3.hasGrade()) {
                        double v1 = ValueUtils.toDoubleMod(p1.getGrade());
                        double v2 = ValueUtils.toDoubleMod(p2.getGrade());
                        double v3 = ValueUtils.toDoubleMod(p3.getGrade());

                        if (v1 < 7 && v2 >= 8.5 && v3 >= 8.5) {
                            Timber.d("Well, that worked");
                            unlocked.put(R.string.achievement_now_all_pieces_come_together, -1);
                        }

                        if (v1 == 7 && v2 == 7 && v3 == 7) {
                            Timber.d("JACKPOT");
                            unlocked.put(R.string.achievement_jackpot, -1);
                        }
                    }
                }
            }
        }

        if (allDisciplinesApproved) unlocked.put(R.string.achievement_clean_semester, -1);
        if (allDisciplinesMechanic) unlocked.put(R.string.achievement_totally_mechanic, -1);
        if (semesterHours >= 480) unlocked.put(R.string.achievement_lend_me_your_time_turner, -1);
        if (semesterHours <= 275) unlocked.put(R.string.achievement_baby_steps, -1);
    }

    private List<GradeInfo> moreThanOne(@NonNull List<GradeSection> sections) {
        List<GradeInfo> infos = new ArrayList<>();
        for (GradeSection section : sections) {
            GradeInfo info = extractGreatest(section);
            if (info != null) infos.add(info);
        }
        return infos;
    }

    @Nullable
    private GradeInfo extractGreatest(GradeSection section) {
        List<GradeInfo> infos = database.gradeInfoDao().getGradesFromSectionDirect(section.getUid());
        GradeInfo greatest = null;
        double maxGrade = Double.NEGATIVE_INFINITY;
        for (GradeInfo info : infos) {
            if (info.hasGrade()) {
                double val = ValueUtils.toDoubleMod(info.getGrade());
                if (val > maxGrade) {
                    greatest = info;
                    maxGrade = val;
                }
            }
        }

        return greatest;
    }

    @WorkerThread
    private void unlockSemesterBased(@Nullable List<Semester> semesters, @NonNull HashMap<Integer, Integer> unlocked) {
        if (semesters == null) return;

        if (semesters.size() > 1) unlocked.put(R.string.achievement_senior_wannabe, -1);
        if (semesters.size() > 4) unlocked.put(R.string.achievement_senior, -1);
        if (semesters.size() > 7) unlocked.put(R.string.achievement_so_when_u_finishing, -1);

        if(semesters.size() >= 3) {
            List<Semester> sorted = new ArrayList<>(semesters);
            Collections.sort(sorted);
            Semester current = Semester.getCurrentSemester(sorted);
            String curSmt = current != null ? current.getName() : "20181";

            int noFinalCount = 0;
            for (Semester semester : sorted) {
                if (semester.getName().equalsIgnoreCase(curSmt)) continue;

                boolean wentToFinal = false;
                boolean invalidated = false;

                List<Discipline> disciplines = database.disciplineDao().getDisciplinesFromSemesterDirect(semester.getName());
                for (Discipline discipline : disciplines) {
                    Grade grade = database.gradeDao().getDisciplineGradesDirect(discipline.getUid());
                    if (grade != null && grade.getFinalScore() != null) {
                        String finalScore = grade.getFinalScore();
                        double val = ValueUtils.toDouble(finalScore.replace(",", "."), -1);

                        if (val < 7 && val != -1) {
                            wentToFinal = true;
                            continue;
                        }
                    }

                    List<GradeSection> sections = database.gradeSectionDao().getSectionsFromDisciplineDirect(discipline.getUid());
                    if (sections.size() > 0) {
                        for (GradeSection section : sections) {
                            String name = section.getName();
                            if (name != null && name.equalsIgnoreCase("notas complementares")) {
                                wentToFinal = true;
                            }
                        }
                    } else {
                        invalidated = true;
                    }
                }

                if (wentToFinal && !invalidated) {
                    noFinalCount = 0;
                } else if (!invalidated){
                    Timber.d("One semester without going to final yay!");
                    noFinalCount++;
                    if (noFinalCount == 3) {
                        unlocked.put(R.string.achievement_killing_spree, -1);
                    }
                }
            }
        }
    }

    private void unlockForSingleGrades(@Nullable List<GradeInfo> infos, @NonNull HashMap<Integer, Integer> unlocked) {
        if (infos == null) return;

        for (GradeInfo info : infos) {
            if (info.getGrade().equalsIgnoreCase("10,0"))
                unlocked.put(R.string.achievement_easy_game, -1);

            if (info.getGrade().equalsIgnoreCase("7,0"))
                unlocked.put(R.string.achievement_mediocre, -1);
        }
    }
}
