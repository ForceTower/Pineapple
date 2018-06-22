package com.forcetower.uefs.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.forcetower.uefs.util.ObjectUtils;

import java.util.Objects;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Entity
public class TodoItem {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @Nullable
    private String disciplineCode;
    private String title;
    @Nullable
    private String message;
    @Nullable
    private String date;
    private boolean hasTimeLimit;
    private boolean completed;
    private boolean shown;

    public TodoItem(@Nullable String disciplineCode, String title, @Nullable String date, boolean hasTimeLimit) {
        this.disciplineCode = disciplineCode;
        this.title = title;
        this.date = date;
        this.hasTimeLimit = hasTimeLimit;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Nullable
    public String getDisciplineCode() {
        return disciplineCode;
    }

    public void setDisciplineCode(@Nullable String disciplineCode) {
        this.disciplineCode = disciplineCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    public String getDate() {
        return date;
    }

    public void setDate(@Nullable String date) {
        this.date = date;
    }

    public boolean isHasTimeLimit() {
        return hasTimeLimit;
    }

    public void setHasTimeLimit(boolean hasTimeLimit) {
        this.hasTimeLimit = hasTimeLimit;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoItem todoItem = (TodoItem) o;
        return ObjectUtils.equals(getTitle(), todoItem.getTitle()) &&
                ObjectUtils.equals(getMessage(), todoItem.getMessage()) &&
                ObjectUtils.equals(getDate(), todoItem.getDate());
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hash(getTitle(), getMessage(), getDate());
    }
}
