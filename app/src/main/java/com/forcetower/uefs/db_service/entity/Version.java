package com.forcetower.uefs.db_service.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by João Paulo on 29/04/2018.
 */
@Entity
public class Version {
    @PrimaryKey(autoGenerate = true)
    private long uid;
    private int code;
    private String name;
    private String details;
    private String download;
    @ColumnInfo(name = "background_image")
    @SerializedName(value = "background_image")
    private String backgroundImage;
    @ColumnInfo(name = "disable_code")
    @SerializedName(value = "disable_code")
    private int disableCode;

    public Version(int code, String name, String details, String download, String backgroundImage, int disableCode) {
        this.code = code;
        this.name = name;
        this.details = details;
        this.download = download;
        this.backgroundImage = backgroundImage;
        this.disableCode = disableCode;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public int getDisableCode() {
        return disableCode;
    }

    public void setDisableCode(int disableCode) {
        this.disableCode = disableCode;
    }
}
