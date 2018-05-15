package com.forcetower.uefs.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
}
