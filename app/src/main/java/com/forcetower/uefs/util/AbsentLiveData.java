package com.forcetower.uefs.util;

import android.arch.lifecycle.LiveData;

@SuppressWarnings("unchecked")
public class AbsentLiveData extends LiveData {
    private AbsentLiveData() {
        postValue(null);
    }
    public static <T> LiveData<T> create() {
        //noinspection unchecked
        return new AbsentLiveData();
    }
}
