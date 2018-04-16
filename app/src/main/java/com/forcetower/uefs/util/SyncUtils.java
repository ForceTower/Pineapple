package com.forcetower.uefs.util;

import com.forcetower.uefs.service.SyncResponse;
import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

import static com.forcetower.uefs.rep.helper.RequestCreator.makeRequestForMainUpdater;

/**
 * Created by João Paulo on 13/03/2018.
 */
public class SyncUtils {

    public static boolean syncCheckMainUpdater(OkHttpClient client, int type) {
        Request request = makeRequestForMainUpdater();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                Gson gson = new Gson();
                SyncResponse sync = gson.fromJson(response.body().string(), SyncResponse.class);
                Timber.d("Sync: %s", sync.isUpdate());
                if (type == 1)
                    return sync.isUpdate();
                else if (type == 2)
                    return sync.isAlarmEnabled();
            }
        } catch (Exception e) {
            Timber.d("Continue due to server offline");
        }
        return true;
    }
}
