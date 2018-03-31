package com.forcetower.uefs.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Mention {
    private int uid;
    @ColumnInfo(name = "credit_id")
    @SerializedName(value = "credit_id")
    private int creditId;
    private String name;
    @Nullable
    private String link;

    public Mention(String name, @Nullable String link) {
        this.name = name;
        this.link = link;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getCreditId() {
        return creditId;
    }

    public void setCreditId(int creditId) {
        this.creditId = creditId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getLink() {
        return link;
    }

    public void setLink(@Nullable String link) {
        this.link = link;
    }
}
