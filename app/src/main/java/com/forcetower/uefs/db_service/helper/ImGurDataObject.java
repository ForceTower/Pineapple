package com.forcetower.uefs.db_service.helper;

/**
 * Created by João Paulo on 16/06/2018.
 */
public class ImGurDataObject {
    private String link;
    private String deletehash;
    private String id;

    public ImGurDataObject(String link, String deletehash, String id) {
        this.link = link;
        this.deletehash = deletehash;
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDeletehash() {
        return deletehash;
    }

    public void setDeletehash(String deletehash) {
        this.deletehash = deletehash;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "L: " + link + " _ ID: " + id + " _ DH: " + deletehash;
    }
}
