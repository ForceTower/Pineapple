package com.forcetower.uefs.rep.helper;

import com.forcetower.uefs.Constants;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import timber.log.Timber;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class RequestCreator {

    public static List<Request> makeListGradeRequests(Document document) {
        List<Request> calls = new ArrayList<>();
        Elements semestersValues = document.select("option");
        for (Element element : semestersValues) {
            Request request = getGradesFor(element.attr("value"), document);
            calls.add(request);
        }
        return calls;
    }

    public static Request getGradesFor(String semesterVal, Document document) {
        FormBody.Builder formBody = new FormBody.Builder();

        Elements elements = document.select("input[value][type=\"hidden\"]");

        for (Element element : elements) {
            String key = element.attr("id");
            String value = element.attr("value");
            formBody.add(key, value);
        }

        formBody.add("ctl00$MasterPlaceHolder$ddPeriodosLetivos$ddPeriodosLetivos", semesterVal);
        formBody.add("ctl00$MasterPlaceHolder$imRecuperar", "Exibir");

        return new Request.Builder()
                .url(Constants.SAGRES_GRADE_ANY)
                .post(formBody.build())
                .addHeader("x-requested-with", "XMLHttpRequest")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();
    }

    public static Request makeLoginRequest(RequestBody body) {
        return new Request.Builder()
                .url(Constants.SAGRES_LOGIN_PAGE)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();
    }

    public static Request makeGradesRequest() {
        return new Request.Builder()
                .url(Constants.SAGRES_GRADE_PAGE)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();
    }

    public static Request makeApprovalRequest(String url, RequestBody body) {
        return new Request.Builder()
                .url("http://" + url)
                .post(body)
                .addHeader("x-requested-with", "XMLHttpRequest")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();
    }

    public static RequestBody makeRequestBody(String username, String password) {
        return new FormBody.Builder()
                .add("ctl00$PageContent$LoginPanel$UserName", username)
                .add("ctl00$PageContent$LoginPanel$Password", password)
                .add("ctl00$PageContent$LoginPanel$LoginButton", "Entrar")
                .add("__EVENTTARGET", "")
                .add("__EVENTARGUMENT", "")
                .add("__VIEWSTATE", Constants.LOGIN_VIEW_STATE)
                .add("__VIEWSTATEGENERATOR", Constants.LOGIN_VW_STT_GEN)
                .add("__EVENTVALIDATION", Constants.LOGIN_VIEW_VALID)
                .build();
    }

    public static RequestBody makeApprovalRequestBody(Document document) {
        FormBody.Builder formBody = new FormBody.Builder();
        Elements elements = document.select("input[value][type=\"hidden\"]");
        for (Element element : elements) {
            String key = element.attr("id");
            String value = element.attr("value");
            Timber.d("K: " + key + " V: " + value);
            formBody.add(key, value);
        }

        formBody.add("ctl00$btnLogin", "Acessar o SAGRES Portal");
        return formBody.build();
    }

    public static Request makeStudentPageRequest() {
        return new Request.Builder()
                .url(Constants.SAGRES_DIARY_PAGE)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();
    }

    public static Request makePostStudentPage(FormBody.Builder builder) {
        return new Request.Builder()
                .url(Constants.SAGRES_DIARY_PAGE)
                .post(builder.build())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();
    }

    public static Request makeRequestClassDetails(FormBody.Builder builder) {
        return new Request.Builder()
                .url(Constants.SAGRES_CLASS_PAGE)
                .post(builder.build())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();
    }


    public static FormBody.Builder makeFormBodyForClassDetails(Document document) {
        FormBody.Builder builder = new FormBody.Builder();
        Elements elements = document.select("input[value][type=\"hidden\"]");
        builder.add("ctl00$MasterPlaceHolder$RowsPerPage1$ddMostrar", "0");
        for (Element elementIn : elements) {
            String id = elementIn.attr("id");
            String val = elementIn.attr("value");
            builder.add(id, val);
        }
        return builder;
    }

    public static Request makeRequestForMainUpdater() {
        return new Request.Builder()
                .url(Constants.MAIN_UPDATER_CONTROL)
                .build();
    }
}
