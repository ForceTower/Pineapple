package com.forcetower.uefs.database.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Entity
@ForeignKey(entity = ADiscipline.class, parentColumns = "uid", childColumns = "discipline", onDelete = CASCADE, onUpdate = CASCADE)
public class ADisciplineGroup {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int discipline;
    private String teacher;
    private String group;
    private int credits;
    private int missLimit;
    private String classPeriod = "";
    private String department = "";
    private String sagresConnectCode = "";
    private boolean draft = true;

    public ADisciplineGroup(int discipline, String teacher, String group, int credits, int missLimit, String classPeriod, String department) {
        this.discipline = discipline;
        this.teacher = teacher;
        this.group = group;
        this.credits = credits;
        this.missLimit = missLimit;
        this.classPeriod = classPeriod;
        this.department = department;
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

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getMissLimit() {
        return missLimit;
    }

    public void setMissLimit(int missLimit) {
        this.missLimit = missLimit;
    }

    public String getClassPeriod() {
        return classPeriod;
    }

    public void setClassPeriod(String classPeriod) {
        this.classPeriod = classPeriod;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSagresConnectCode() {
        return sagresConnectCode;
    }

    public void setSagresConnectCode(String sagresConnectCode) {
        this.sagresConnectCode = sagresConnectCode;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }
}
