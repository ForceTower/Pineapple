package com.forcetower.uefs;

import android.app.Application;

import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresClass;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDay;

import java.util.HashMap;
import java.util.List;

/**
 * Created by João Paulo on 09/11/2017.
 */

public class UEFSApplication extends Application {
    private String html;
    private HashMap<String, SagresClass> classes;
    private HashMap<String, List<SagresClassDay>> schedule;

    public UEFSApplication() {
        super();
    }

    public void saveHtml(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public void saveClasses(HashMap<String, SagresClass> classes) {
        this.classes = classes;
        schedule = Utils.getSchedule(classes);
    }

    public HashMap<String, SagresClass> getClasses() {
        return classes;
    }

    public HashMap<String, List<SagresClassDay>> getSchedule() {
        return schedule;
    }
}
