package com.forcetower.uefs.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Entity
@ForeignKey(entity = Semester.class, parentColumns = "name", childColumns = "semester", onUpdate = CASCADE, onDelete = CASCADE)
public class Discipline {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String semester;
    private String name;
    private String code;
    private int credits;
    @ColumnInfo(name = "missed_classes")
    private int missedClasses;
    @ColumnInfo(name = "missed_classes_informed")
    private int missedClassesInformed;
    @ColumnInfo(name = "last_class")
    private String lastClass = "0";
    @ColumnInfo(name = "next_class")
    private String nextClass = "0";
    private String situation;

    @Ignore
    private List<GradeSection> sections;
    @Ignore
    private Grade grade;

    public Discipline(String semester, String name, String code) {
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

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getMissedClasses() {
        return missedClasses;
    }

    public void setMissedClasses(int missedClasses) {
        this.missedClasses = missedClasses;
    }

    public int getMissedClassesInformed() {
        return missedClassesInformed;
    }

    public void setMissedClassesInformed(int missedClassesInformed) {
        this.missedClassesInformed = missedClassesInformed;
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

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    @Override
    public String toString() {
        return "Code: " + code + " - Semester: " + semester;
    }

    public void setSections(List<GradeSection> sections) {
        this.sections = sections;
    }

    public List<GradeSection> getSections() {
        return sections;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }


    public boolean equals(Object o) {
        if (o instanceof Discipline) {
            Discipline d = (Discipline) o;
            return code.equalsIgnoreCase(d.code);
        }
        return false;
    }
}
