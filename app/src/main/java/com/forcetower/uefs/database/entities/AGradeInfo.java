package com.forcetower.uefs.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Entity
@ForeignKey(entity = AGradeSection.class, parentColumns = "uid", childColumns = "section", onUpdate = CASCADE, onDelete = CASCADE)
public class AGradeInfo {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int section;
    @ColumnInfo(name = "evaluation_name")
    private String evaluationName;
    private String grade;
    private String date;

    public AGradeInfo(int section, String evaluationName, String grade, String date) {
        this.section = section;
        this.evaluationName = evaluationName;
        this.grade = grade;
        this.date = date;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public String getEvaluationName() {
        return evaluationName;
    }

    public void setEvaluationName(String evaluationName) {
        this.evaluationName = evaluationName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
