package com.forcetower.uefs.db_service.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

@Entity(foreignKeys = {
        @ForeignKey(entity = CreditsMention.class, parentColumns = "uid", childColumns = "credit_id", onDelete = ForeignKey.CASCADE)
}, indices = {
        @Index(value = "credit_id")
})
public class Mention {
    @PrimaryKey(autoGenerate = true)
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
