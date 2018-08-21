package com.forcetower.uefs.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by João Paulo on 23/06/2018.
 */
@Entity
public class DisciplineMissedClass {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String date;
    private String description;
    private int disciplineId;

    @Ignore
    private String disciplineCode;

    @Ignore
    public DisciplineMissedClass(String disciplineCode) {
        this.disciplineCode = disciplineCode;
    }

    public DisciplineMissedClass(String date, String description) {
        this.date = date;
        this.description = description;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDisciplineId() {
        return disciplineId;
    }

    public void setDisciplineId(int disciplineId) {
        this.disciplineId = disciplineId;
    }

    public String getDisciplineCode() {
        return disciplineCode;
    }

    public void setDisciplineCode(String disciplineCode) {
        this.disciplineCode = disciplineCode;
    }
}
