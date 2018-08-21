package com.forcetower.uefs.db_service.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by João Paulo on 09/06/2018.
 */
@Entity
public class AboutField {
    @PrimaryKey(autoGenerate = true)
    @SerializedName(value = "id")
    private int uid;
    private String title;
    private String message;
    private boolean active;
    private String link;

    public AboutField(String title, String message) {
        this.title = title;
        this.message = message;
        this.active = true;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return title + " :: " + message;
    }
}
