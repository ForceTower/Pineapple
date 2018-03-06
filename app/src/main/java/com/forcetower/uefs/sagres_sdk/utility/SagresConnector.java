package com.forcetower.uefs.sagres_sdk.utility;

import android.util.Log;

import com.forcetower.uefs.sagres_sdk.SagresConstants;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by João Paulo on 18/11/2017.
 */

public class SagresConnector {

    public static JSONObject login(String username, String password) throws JSONException {
        RequestBody requestBody = new FormBody.Builder()
                .add("__EVENTTARGET", "")
                .add("__EVENTARGUMENT", "")
                .add("__VIEWSTATE", "/wEPDwUKMTc5MDkxMTc2NA9kFgJmD2QWBAIBD2QWDAIEDxYCHgRocmVmBT1+L0FwcF9UaGVtZXMvTmV3VGhlbWUvQWNlc3NvRXh0ZXJuby5jc3M/ZnA9NjM2Mzk4MDY3NDQwMDAwMDAwZAIFDxYCHwAFOH4vQXBwX1RoZW1lcy9OZXdUaGVtZS9Db250ZXVkby5jc3M/ZnA9NjM2Mzk4MDY3NDQwMDAwMDAwZAIGDxYCHwAFOX4vQXBwX1RoZW1lcy9OZXdUaGVtZS9Fc3RydXR1cmEuY3NzP2ZwPTYzNjIxNDcxMjMwMDAwMDAwMGQCBw8WAh8ABTl+L0FwcF9UaGVtZXMvTmV3VGhlbWUvTWVuc2FnZW5zLmNzcz9mcD02MzYyMTQ3MTIzMDAwMDAwMDBkAggPFgIfAAU2fi9BcHBfVGhlbWVzL05ld1RoZW1lL1BvcFVwcy5jc3M/ZnA9NjM2MjE0NzEyMzAwMDAwMDAwZAIJDxYCHwAFWC9Qb3J0YWwvUmVzb3VyY2VzL1N0eWxlcy9BcHBfVGhlbWVzL05ld1RoZW1lL05ld1RoZW1lMDEvZXN0aWxvLmNzcz9mcD02MzYxMDU4MjY2NDAwMDAwMDBkAgMPZBYEAgcPDxYEHgRUZXh0BQ1TYWdyZXMgUG9ydGFsHgdWaXNpYmxlaGRkAgsPZBYGAgEPDxYCHwJoZGQCAw88KwAKAQAPFgIeDVJlbWVtYmVyTWVTZXRoZGQCBQ9kFgICAg9kFgICAQ8WAh4LXyFJdGVtQ291bnRmZGTS+Y3bntF2UZMwIIXP8cpv13rKAw==")
                .add("__VIEWSTATEGENERATOR", "BB137B96")
                .add("__EVENTVALIDATION", "/wEdAATbze7D9s63/L1c2atT93YlM4nqN81slLG8uEFL8sVLUjoauXZ8QTl2nEJmPx53FYhjUq3W1Gjeb7bKHHg4dlob4GWO7EiBlTRJt8Yw8hywpn30EZA=")
                .add("ctl00$PageContent$LoginPanel$UserName", username)
                .add("ctl00$PageContent$LoginPanel$Password", password)
                .add("ctl00$PageContent$LoginPanel$LoginButton", "Entrar")
                .build();

        final Request request = new Request.Builder()
                .url(SagresConstants.SAGRES_LOGIN_PAGE)
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();

        Call call = SagresPortalSDK.getHttpClient().newCall(request);
        JSONObject jsonObject = new JSONObject();
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                String currentLocation = response.request().url().url().getHost() + response.request().url().url().getPath();
                Log.d(SagresPortalSDK.SAGRES_SDK_TAG, "Current location: " + currentLocation);

                String body = response.body().string();
                body = checkApprovalNeeded(body, currentLocation);
                if (body == null) {
                    jsonObject.put("error", 1);
                } else {
                    jsonObject.put("html", body);
                }
                return jsonObject;
            } else {
                jsonObject.put("error", 1);
            }
        } catch (IOException e) {
            jsonObject.put("error", 0);
        }

        return jsonObject;
    }

    private static String checkApprovalNeeded(String body, String url) {
        Document document = Jsoup.parse(body);

        Element approval = document.selectFirst("div[class=\"acesso-externo-pagina-login\"]");
        if (approval != null) {
            return approve(document, url);
        } else {
            approval = document.selectFirst("input[value=\"Acessar o SAGRES Portal\"]");
            if (approval == null)
                return body;
            else {
                return approve(document, url);
            }
        }
    }

    private static String approve(Document document, String url) {
        Log.d(SagresPortalSDK.SAGRES_SDK_TAG, "Approval needed");
        FormBody.Builder formBody = new FormBody.Builder();

        Elements elements = document.select("input[value][type=\"hidden\"]");
        for (Element element : elements) {
            String key = element.attr("id");
            String value = element.attr("value");
            Log.d(SagresPortalSDK.SAGRES_SDK_TAG, "K: " + key + " V: " + value);
            formBody.add(key, value);
        }

        formBody.add("ctl00$btnLogin", "Acessar o SAGRES Portal");

        Request request = new Request.Builder()
                .url("http://" + url)
                .post(formBody.build())
                .addHeader("x-requested-with", "XMLHttpRequest")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();

        Call call = SagresPortalSDK.getHttpClient().newCall(request);

        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }

    }

    static JSONObject getStudentGrades () throws JSONException {
        Request request = new Request.Builder()
                .url(SagresConstants.SAGRES_GRADE_PAGE)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();

        Call call = SagresPortalSDK.getHttpClient().newCall(request);

        JSONObject jsonObject = new JSONObject();
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                jsonObject.put("html", response.body().string());
                return jsonObject;
            } else {
                jsonObject.put("error", 1);
            }
        } catch (IOException e) {
            jsonObject.put("error", 0);
        }

        return jsonObject;
    }

    public static String getSagresStudentPage() {
        Request request = new Request.Builder()
                .url(SagresConstants.SAGRES_DIARY_PAGE)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();

        Call call = SagresPortalSDK.getHttpClient().newCall(request);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
            else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
