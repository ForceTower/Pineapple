package com.forcetower.uefs.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Entity
@ForeignKey(entity = Discipline.class, parentColumns = "uid", childColumns = "discipline", onDelete = CASCADE, onUpdate = CASCADE)
public class GradeSection {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int discipline;
    private String name;
    @ColumnInfo(name = "partial_mean")
    private String partialMean;

    @Ignore
    private List<GradeInfo> grades;

    @Ignore
    private String disciplineName;

    public GradeSection(int discipline, String name) {
        this.discipline = discipline;
        this.name = name;
        grades = new ArrayList<>();
    }

    @Ignore
    public GradeSection(String name, String discipline) {
        this.name = name;
        this.disciplineName = discipline;
        grades = new ArrayList<>();
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getDiscipline() {
        return discipline;
    }

    public void setDiscipline(int discipline) {
        this.discipline = discipline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartialMean() {
        return partialMean;
    }

    public void setPartialMean(String partialMean) {
        this.partialMean = partialMean;
    }

    public String getDisciplineName() {
        return disciplineName;
    }

    public List<GradeInfo> getGrades() {
        return grades;
    }

    public void addGrade(GradeInfo info) {
        grades.add(info);
    }

    public void copySelective(@NonNull GradeSection other) {
        if (getPartialMean() == null || other.getPartialMean() != null) setPartialMean(other.getPartialMean());
    }

    public void setGradeInfos(List<GradeInfo> infos) {
        grades.addAll(infos);
    }
}
