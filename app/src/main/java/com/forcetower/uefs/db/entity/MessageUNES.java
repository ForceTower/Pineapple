package com.forcetower.uefs.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.SpannableString;

@Entity(indices = {
        @Index(value = "uuid", unique = true)
})
public class MessageUNES {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @NonNull
    private String creator;
    @NonNull
    private String message;
    @ColumnInfo(name = "create_date")
    private long createDate;
    @Nullable
    @ColumnInfo(name = "image_url")
    private String imageUrl;
    @NonNull
    private String uuid;
    @Nullable
    private String title;

    @Ignore
    private SpannableString spannable;

    public MessageUNES(@NonNull String creator, @NonNull String message, long createDate, @Nullable String imageUrl, @NonNull String uuid, @Nullable String title) {
        this.creator = creator;
        this.message = message;
        this.createDate = createDate;
        this.imageUrl = imageUrl;
        this.uuid = uuid;
        this.title = title;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @NonNull
    public String getCreator() {
        return creator;
    }

    public void setCreator(@NonNull String creator) {
        this.creator = creator;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NonNull String message) {
        this.message = message;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    public void setSpannable(SpannableString spannable) {
        this.spannable = spannable;
    }

    public SpannableString getSpannable() {
        return spannable;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }
}
