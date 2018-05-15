package com.forcetower.uefs.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import timber.log.Timber;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Created by João Paulo on 05/03/2018.
 * Notified = 0 -> no notifications
 * Notified = 1 -> new grade posted
 * Notified = 2 -> recently created
 * Notified = 3 -> av date modified
 */
@Entity
@ForeignKey(entity = GradeSection.class, parentColumns = "uid", childColumns = "section", onUpdate = CASCADE, onDelete = CASCADE)
public class GradeInfo {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int section;
    @ColumnInfo(name = "evaluation_name")
    private String evaluationName;
    private String grade;
    private String date;
    private int notified;
    private double weight;
    private boolean lost;

    @Ignore
    private String className;

    public GradeInfo(int section, String evaluationName, String grade, String date) {
        this.section = section;
        this.evaluationName = evaluationName;
        this.grade = grade;
        this.date = date;
        this.lost = false;
    }

    @Ignore
    public GradeInfo(String evaluationName, String grade, String date) {
        this.evaluationName = evaluationName;
        this.grade = grade;
        this.date = date;
        lost = false;
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

    public int getNotified() {
        return notified;
    }

    public void setNotified(int notified) {
        if (this.notified != notified)
            Timber.d("Changed notified from %d to %d", this.notified, notified);
        this.notified = notified;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void selectiveCopy(@NonNull GradeInfo other) {
        if (other.getDate() != null && !other.getDate().equalsIgnoreCase(getDate())) {
            setDate(other.getDate());
            setNotified(3);
        }

        if (this.getGrade() == null) {
            setGrade(other.getGrade());
            setNotified(other.hasGrade() ? 1 : 2);
        } else if (other.hasGrade()) { //Added condition, can't change grade to not defined
            if (!getGrade().trim().equalsIgnoreCase(other.getGrade().trim())) {
                Timber.d("Grade changed: Before(%s) After(%s)", getGrade().trim(), other.getGrade().trim());
                setGrade(other.getGrade());
                setNotified(1);
            }
        }
    }

    public boolean isLost() {
        return lost;
    }

    public void setLost(boolean lost) {
        this.lost = lost;
    }

    @Override
    public String toString() {
        //return "evaluation name: " + getEvaluationName() + " - grade: " + getGrade();
        return uid + "";
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean hasGrade() {
        return grade != null && !grade.trim().isEmpty()
                && !grade.trim().equalsIgnoreCase("Não Divulgada")
                && !grade.trim().equalsIgnoreCase("-")
                && !grade.trim().equalsIgnoreCase("--")
                && !grade.trim().equalsIgnoreCase("*")
                && !grade.trim().equalsIgnoreCase("**")
                && !grade.trim().equalsIgnoreCase("-1");
    }

    public double getCalculatedValue() {
        if (weight == 0 || getGrade() != null) {
            if (getGrade().equalsIgnoreCase("Não Divulgada")) {
                return -1;
            }

            String grade = getGrade();
            grade = grade.replace(",", ".");
            if (grade.endsWith("*")) grade = grade.substring(0, grade.length() - 1);
            try {
                return Double.parseDouble(grade) * weight;
            } catch (Exception e) {
                Timber.d("unable to parse double: " + grade + " or " + weight);
                return -1;
            }
        }
        return -1;
    }
}
