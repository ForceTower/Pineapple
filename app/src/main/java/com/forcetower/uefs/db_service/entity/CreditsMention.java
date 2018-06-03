package com.forcetower.uefs.db_service.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity
public class CreditsMention {
    @PrimaryKey(autoGenerate = true)
    @SerializedName(value = "id")
    private int uid;
    private String category;

    public CreditsMention(String category) {
        this.category = category;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
