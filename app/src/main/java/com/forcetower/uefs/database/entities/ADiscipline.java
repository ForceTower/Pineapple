package com.forcetower.uefs.database.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Entity
@ForeignKey(entity = ASemester.class, parentColumns = "uefs_id", childColumns = "semester", onUpdate = ForeignKey.CASCADE)
public class ADiscipline {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String semester;
    private String name;
    private String code;
    private String missedClasses = "0";
    private String lastClass = "0";
    private String nextClass = "0";
    private String missedClassesInformed = "0";

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

    public String getMissedClasses() {
        return missedClasses;
    }

    public void setMissedClasses(String missedClasses) {
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

    public String getMissedClassesInformed() {
        return missedClassesInformed;
    }

    public void setMissedClassesInformed(String missedClassesInformed) {
        this.missedClassesInformed = missedClassesInformed;
    }
}
