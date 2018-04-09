package com.forcetower.uefs.sgrs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import okhttp3.Response;
import timber.log.Timber;

/**
 * Created by João Paulo on 06/03/2018.
 */

public class SagresResponse {
    private Response response;
    private Document document;
    private String message;
    private Throwable throwable;
    private int code;

    public SagresResponse(Response response) {
        code = response.code();
        this.response = response;
        if (response.isSuccessful()) {
            Timber.d("Successful response");
            try {
                String body = response.body().string();
                document = Jsoup.parse(body);
                message = null;
            } catch (Exception ignored) {
                Timber.d("Failed parsing success body, %s", ignored.getMessage());
                message = ignored.getMessage();
                Timber.e("This happened: %s", ignored.getMessage());
                code = 500;
                throwable = ignored;
            }
        } else {
            Timber.d("Unsuccessful response");
            try {
                message = response.body().string();
            } catch (Exception ignored) {
                Timber.d("Failed parsing failed body, %s", ignored.getMessage());
                message = ignored.getMessage();
                Timber.e("This happened: %s", ignored.getMessage());
                throwable = ignored;
            } finally {
                code = 500;
            }
        }
    }

    public SagresResponse(Throwable throwable) {
        this.throwable = throwable;
        code = 500;
        message = throwable.getMessage();
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

    public Document getDocument() {
        return document;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public int getCode() {
        return code;
    }

    public Response getResponse() {
        return response;
    }
}
