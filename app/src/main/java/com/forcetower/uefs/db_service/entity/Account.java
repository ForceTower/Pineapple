package com.forcetower.uefs.db_service.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.Nullable;

import com.forcetower.uefs.Constants;
import com.google.gson.annotations.SerializedName;

@Entity(indices = {
        @Index(value = "username", unique = true)
})
public class Account {
    @PrimaryKey(autoGenerate = true)
    private long uid;
    private String username;
    private String name;
    @SerializedName(value = "image_url")
    @Nullable
    private String imageUrl;
    @ColumnInfo(name = "inserted_at")
    private long insertedAt;

    public Account(String username, String name, @Nullable String imageUrl) {
        this.username = username;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(long createdAt) {
        this.insertedAt = createdAt;
    }

    public boolean isOutdated() {
        long current = System.currentTimeMillis()/1000;
        long limit = insertedAt + Constants.OUTDATED_SECONDS;
        return current > limit;
    }
}