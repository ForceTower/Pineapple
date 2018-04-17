package com.forcetower.uefs.service;

import com.google.gson.annotations.SerializedName;

/**
 * Created by João Paulo on 01/04/2018.
 */
public class Version {
    @SerializedName(value = "version_code")
    private int versionCode;
    @SerializedName(value = "version_name")
    private String versionName;
    @SerializedName(value = "whats_new")
    private String whatsNew;
    @SerializedName(value = "download_link")
    private String downloadLink;
    @SerializedName(value = "disable_code")
    private int disableCode;
    @SerializedName(value = "new_schedule")
    private int newSchedule;
    @SerializedName(value = "background_image")
    private String backgroundImage;

    public Version(int versionCode, String versionName, String whatsNew, String downloadLink, int disableCode, int newSchedule, String backgroundImage) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.whatsNew = whatsNew;
        this.downloadLink = downloadLink;
        this.disableCode = disableCode;
        this.newSchedule = newSchedule;
        this.backgroundImage = backgroundImage;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getWhatsNew() {
        return whatsNew;
    }

    public void setWhatsNew(String whatsNew) {
        this.whatsNew = whatsNew;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public int getDisableCode() {
        return disableCode;
    }

    public void setDisableCode(int disableCode) {
        this.disableCode = disableCode;
    }

    public int getNewSchedule() {
        return newSchedule;
    }

    public void setNewSchedule(int newSchedule) {
        this.newSchedule = newSchedule;
    }

    public boolean showNewSchedule() {
        return newSchedule == 1;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }
}
