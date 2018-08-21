package com.forcetower.uefs.rep;

import androidx.lifecycle.LiveData;
import android.content.Context;
import android.graphics.Bitmap;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db_service.helper.ImGurDataObject;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.resources.SendNotificationResource;
import com.forcetower.uefs.rep.resources.UploadToImGurResource;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.UNEService;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import timber.log.Timber;

import static com.forcetower.uefs.rep.helper.RequestCreator.makeFormBodyForImGurImageUpload;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeRequestForImGurImageUpload;

@Singleton
public class NotificationRepository {
    private final AppExecutors executors;
    private final OkHttpClient client;
    private final String imGurAlbum;
    private final String imGurSecret;
    private final UNEService service;

    @Inject
    public NotificationRepository(AppExecutors executors, OkHttpClient client, Context context, UNEService service) {
        this.executors = executors;
        this.client = client;
        this.imGurAlbum = context.getString(R.string.imgur_service_album);
        this.imGurSecret = context.getString(R.string.imgur_service_secret);
        this.service = service;
    }

    public LiveData<Resource<ImGurDataObject>> uploadImageToImGur(Bitmap bitmap, String name) {
        return new UploadToImGurResource(executors, bitmap) {

            @Override
            protected Call createCall(String base64) {
                FormBody.Builder builder = makeFormBodyForImGurImageUpload(base64, imGurAlbum, name);
                Request request = makeRequestForImGurImageUpload(builder.build(), imGurSecret);
                return client.newCall(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<ActionResult<String>>> sendNotification(String title, String message, String link, String deleteHash) {
        return new SendNotificationResource(executors) {

            @Override
            protected LiveData<ApiResponse<ActionResult<String>>> createCall() {
                if (link != null && deleteHash != null) {
                    Timber.d("Invoke Native #1");
                    return service.sendNotification(title, message, link, deleteHash, UUID.randomUUID().toString());
                } else {
                    Timber.d("Invoke Native #2");
                    return service.sendNotification(title, message, UUID.randomUUID().toString());
                }
            }
        }.asLiveData();
    }
}
