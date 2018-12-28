package com.forcetower.uefs.rep.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.forcetower.uefs.Constants;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Timber.d("Semester Value is %s", semesterVal);
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

    @NonNull
    public static Request makeGradesRequestForSemester(long semester, @NonNull Document document, @Nullable Long variant) {
        FormBody.Builder formBody = new FormBody.Builder();

        Elements elements = document.select("input[value][type=\"hidden\"]");

        for (Element element : elements) {
            String key = element.attr("id");
            String value = element.attr("value");
            formBody.add(key, value);
        }

        formBody.add("ctl00$MasterPlaceHolder$ddPeriodosLetivos$ddPeriodosLetivos", Long.valueOf(semester).toString());
        if (variant != null) {
            formBody.add("ctl00$MasterPlaceHolder$ddRegistroCurso", variant.toString());
        }
        formBody.add("ctl00$MasterPlaceHolder$imRecuperar", "Exibir");
        return new Request.Builder()
                .url(Constants.SAGRES_GRADE_ANY)
                .post(formBody.build())
                .addHeader("x-requested-with", "XMLHttpRequest")
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

    public static Request makeRequestForEnrollmentCertificate() {
        return new Request.Builder()
                .url(Constants.SAGRES_ENROLL_CERT)
                .build();
    }

    public static Request makeRequestForFlowchart() {
        return new Request.Builder()
                .url(Constants.SAGRES_FLOWCHART)
                .build();
    }

    public static Request makeRequestForScholarHistory() {
        return new Request.Builder()
                .url(Constants.SAGRES_HISTORY)
                .build();
    }

    public static Request makeRequestForURL(String url) {
        return new Request.Builder().url(url).build();
    }

    public static FormBody.Builder makeFormBodyForDisciplineDetails(@NonNull Document document, @NonNull String encoded) {
        FormBody.Builder builderIn = new FormBody.Builder();

        HashMap<String, String> values = new HashMap<>();
        values.put("ctl00$MasterPlaceHolder$RowsPerPage1$ddMostrar", "0");

        Elements elements = document.select("input[value][type=\"hidden\"]");

        for (Element elementIn : elements) {
            String id = elementIn.attr("id");
            String val = elementIn.attr("value");
            values.put(id, val);
        }

        values.put("__aspnetForm_ClientStateInput", encoded);
        values.put("ctl00$smpManager", "ctl00$MasterPlaceHolder$UpdatePanel1|ctl00$MasterPlaceHolder$pvMaterialApoio");
        values.put("_ajax_ctl00_MasterPlaceHolder_dwForm_context", "(objctl00_MasterPlaceHolder_dwForm 0)(21890 )((currentrow 0)(sortString '?'))");
        values.put("_ajax_ctl00_MasterPlaceHolder_dwForm_client", "(scrollbar 0 0)");
        values.put("_ajax_ctl00_MasterPlaceHolder_ucPopupConsultaMaterialApoio_dwForm_context", "(objctl00_MasterPlaceHolder_ucPopupConsultaMaterialApoio_dwForm 0)(22022 )((sortString 'anx_ds_anexo A'))");
        values.put("_ajax_ctl00_MasterPlaceHolder_ucPopupConsultaMaterialApoio_dwForm_client", "(scrollbar 0 0)");
        values.put("__EVENTTARGET", "ctl00$MasterPlaceHolder$pvMaterialApoio");
        values.put("__EVENTARGUMENT", "true");
        values.put("__ctl00_MasterPlaceHolder_pvMaterialApoio_ClientStateInput", "eyJfcmVhbFR5cGUiOnRydWUsInNob3ckX2luc2VydE5ld1JvdyI6ZmFsc2V9");
        values.put("__ctl00_MasterPlaceHolder_ucPopupConsultaMaterialApoio_ClientStateInput", "eyJfcmVhbFR5cGUiOnRydWV9");
        values.put("__ctl00_MasterPlaceHolder_ucPopupConsultaPlanoAula_PopupView1_ClientStateInput", "eyJfcmVhbFR5cGUiOnRydWV9");
        values.put("ctl00$HeaderPlaceHolder$ucCabecalhoClasse$PainelRetratil1_ClientState", "true");
        values.put("__ASYNCPOST", "false");

        for (Map.Entry<String, String> value : values.entrySet()) {
            builderIn.add(value.getKey(), value.getValue());
        }

        return builderIn;
    }

    public static FormBody.Builder makeFormBodyForImGurImageUpload(String image, String album, String name) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("image", image);
        builder.add("album", album);
        builder.add("name",  name);
        return builder;
    }

    public static Request makeRequestForImGurImageUpload(FormBody body, String secret) {
        return new Request.Builder()
                .url("https://api.imgur.com/3/image")
                .addHeader("Authorization", secret)
                .post(body)
                .build();
    }
}
