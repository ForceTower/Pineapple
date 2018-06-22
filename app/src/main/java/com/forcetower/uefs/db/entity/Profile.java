package com.forcetower.uefs.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by João Paulo on 06/03/2018.
 */
@Entity
public class Profile {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String name;
    private double score;
    @ColumnInfo(name = "last_sync")
    private long lastSync;
    @ColumnInfo(name = "last_sync_attempt")
    private long lastSyncAttempt;
    private String course;

    public Profile(String name, double score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getLastSync() {
        return lastSync;
    }

    public void setLastSync(long lastSync) {
        this.lastSync = lastSync;
    }

    public long getLastSyncAttempt() {
        return lastSyncAttempt;
    }

    public void setLastSyncAttempt(long lastSyncAttempt) {
        this.lastSyncAttempt = lastSyncAttempt;
    }

    public String getCourse() {
        return course;
    }

    public String getCourseFixed() {
        if (getCourse() == null) return "";
        return getCourse().replace("/", "");
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
