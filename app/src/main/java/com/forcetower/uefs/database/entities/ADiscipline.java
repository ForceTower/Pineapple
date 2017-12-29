package com.forcetower.uefs.database.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Entity
@ForeignKey(entity = ASemester.class, parentColumns = "uefs_id", childColumns = "semester", onUpdate = CASCADE, onDelete = CASCADE)
public class ADiscipline {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String semester;
    private String name;
    private String code;
    private int credits;
    private int missedClasses;
    private int missedClassesInformed;
    private String lastClass = "0";
    private String nextClass = "0";

    public ADiscipline(String semester, String name, String code) {
        this.semester = semester;
        this.name = name;
        this.code = code;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getMissedClasses() {
        return missedClasses;
    }

    public void setMissedClasses(int missedClasses) {
        this.missedClasses = missedClasses;
    }

    public String getLastClass() {
        return lastClass;
    }

    public void setLastClass(String lastClass) {
        this.lastClass = lastClass;
    }

    public String getNextClass() {
        return nextClass;
    }

    public void setNextClass(String nextClass) {
        this.nextClass = nextClass;
    }

    public int getMissedClassesInformed() {
        return missedClassesInformed;
    }

    public void setMissedClassesInformed(int missedClassesInformed) {
        this.missedClassesInformed = missedClassesInformed;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    @Override
    public String toString() {
        return semester + " " + code + " " + name;
    }
}
