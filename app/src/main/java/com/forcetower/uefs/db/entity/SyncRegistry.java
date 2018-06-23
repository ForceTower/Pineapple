package com.forcetower.uefs.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.forcetower.uefs.util.DateUtils;

/**
 * Created by João Paulo on 23/06/2018.
 */
@Entity
public class SyncRegistry {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private long attempt;
    @Nullable
    private Long completed;

    public SyncRegistry(long attempt) {
        this.attempt = attempt;
        this.completed = null;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getAttempt() {
        return attempt;
    }

    public void setAttempt(long attempt) {
        this.attempt = attempt;
    }

    @Nullable
    public Long getCompleted() {
        return completed;
    }

    public void setCompleted(@Nullable Long completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Att: " + attempt + " :: Com: " + completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SyncRegistry registry = (SyncRegistry) o;

        if (getUid() != registry.getUid()) return false;
        if (getAttempt() != registry.getAttempt()) return false;
        return getCompleted() != null ? getCompleted().equals(registry.getCompleted()) : registry.getCompleted() == null;
    }

    @Override
    public int hashCode() {
        int result = getUid();
        result = 31 * result + (int) (getAttempt() ^ (getAttempt() >>> 32));
        result = 31 * result + (getCompleted() != null ? getCompleted().hashCode() : 0);
        return result;
    }
}
