package com.forcetower.uefs.db_service.helper;

import com.google.gson.annotations.SerializedName;

/**
 * Created by João Paulo on 16/06/2018.
 */
public class ImGurDataObject {
    private String link;
    @SerializedName("deletehash")
    private String deleteHash;
    private String id;

    public ImGurDataObject(String link, String deleteHash, String id) {
        this.link = link;
        this.deleteHash = deleteHash;
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDeleteHash() {
        return deleteHash;
    }

    public void setDeleteHash(String deleteHash) {
        this.deleteHash = deleteHash;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "L: " + link + " _ ID: " + id + " _ DH: " + deleteHash;
    }
}
