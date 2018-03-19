package com.forcetower.uefs.rep;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.sgrs.SagresResponse;
import com.forcetower.uefs.util.network.LiveDataCallAdapter;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import timber.log.Timber;

/**
 * Created by João Paulo on 09/03/2018.
 */
@SuppressWarnings("WeakerAccess")
public abstract class FetchClassDetailsResource {
    private final AppExecutors executors;
    @Nullable
    private final String semester;
    @Nullable
    private final String classCode;
    @Nullable
    private final String classType;
    private final MediatorLiveData<Resource<Integer>> result;

    private final Document document;
    private final List<Pair<FormBody.Builder,String>> builderList = new ArrayList<>();

    @MainThread
    FetchClassDetailsResource(AppExecutors executors, @Nullable String semester, @Nullable String classCode, @Nullable String classType, @NonNull Document document) {
        this.executors = executors;
        this.semester = semester;
        this.classCode = classCode;
        this.classType = classType;
        result = new MediatorLiveData<>();
        this.document = document;
        Timber.d("Fetch Class details called for %s %s %s", semester, classCode, classType);
        result.postValue(Resource.loading(R.string.connecting));
        
        fetchFromSagres();
    }

    private void fetchFromSagres() {
        executors.diskIO().execute(() -> {
            preProcess();
            afterProcess();
        });
    }

    private void afterProcess() {
        for (Pair<FormBody.Builder, String> pair : builderList) {
            try {
                preConnect(pair.first, pair.second);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void preConnect(FormBody.Builder builder, String semester) {
        Timber.d("Pre connecting...");
        executors.diskIO().execute(() ->{
            Call call = createCallPreConnect(builder);
            LiveData<SagresResponse> respSrc = LiveDataCallAdapter.adapt(call);
            result.addSource(respSrc, preResp -> {
                result.removeSource(respSrc);
                Timber.d("Connected");
                //noinspection ConstantConditions
                if (preResp.isSuccessful()) {
                    Document conDoc = preResp.getDocument();
                    executors.diskIO().execute(() -> {
                        Call connect = makeFinalConnectCall(conDoc);
                        LiveData<SagresResponse> detResp = LiveDataCallAdapter.adapt(connect);
                        result.addSource(detResp, response -> {
                            result.removeSource(detResp);
                            //noinspection ConstantConditions
                            if (response.isSuccessful()) {
                                Document classDetails = response.getDocument();
                                executors.diskIO().execute(() -> {
                                    saveResult(classDetails);
                                    result.postValue(Resource.success(R.string.completed));
                                });
                            } else {
                                result.postValue(Resource.error(response.getMessage(), response.getCode(), R.string.failed_to_connect));
                            }
                        });
                    });
                } else {
                    result.postValue(Resource.error(preResp.getMessage(), preResp.getCode(), R.string.failed_to_connect));
                }
            });
        });
    }

    private void preProcess() {
        Elements classes = document.select("section[class=\"webpart-aluno-item\"]");
        for (Element classDet : classes) {
            String title = classDet.selectFirst("a[class=\"webpart-aluno-nome cor-destaque\"]").text();
            String period = classDet.selectFirst("span[class=\"webpart-aluno-periodo\"]").text();

            int codePos = title.indexOf("-");
            String code = title.substring(0, codePos).trim();

            Elements elements = document.select("input[value][type=\"hidden\"]");
            Element ul = classDet.selectFirst("ul");

            if (ul != null) {
                Elements lis = ul.select("li");
                for (Element li : lis) {
                    Element element = li.selectFirst("a[href]");
                    String values = element.attr("href");
                    int start = values.indexOf("'");
                    values = values.substring(start + 1);
                    int end = values.indexOf("'");

                    values = values.substring(0, end);
                    String type = element.text();
                    int refGroupPos = type.lastIndexOf("(");
                    type = type.substring(0, refGroupPos).trim();

                    FormBody.Builder builderIn = new FormBody.Builder();
                    for (Element elementIn : elements) {
                        String key = elementIn.attr("id");
                        String value = elementIn.attr("value");
                        builderIn.add(key, value);
                    }
                    builderIn.add("__EVENTTARGET", values);
                    if (semester == null || period.equalsIgnoreCase(semester)) {
                        if (classCode == null || code.equalsIgnoreCase(classCode)) {
                            if (classType == null || type.equalsIgnoreCase(classType)) {
                                builderList.add(new Pair<>(builderIn, period));
                            }
                        }
                    }
                }
            } else {
                Element webPart = classDet.selectFirst("div[class=\"webpart-dropdown webpart-dropdown-up\"]");
                Element anchor = webPart.selectFirst("a[href]");
                String values = anchor.attr("href");
                int start = values.indexOf("'");
                values = values.substring(start + 1);
                int end = values.indexOf("'");

                values = values.substring(0, end);

                FormBody.Builder builder = new FormBody.Builder();
                builder.add("__EVENTTARGET", values);

                for (Element element : elements) {
                    String key = element.attr("id");
                    String value = element.attr("value");
                    builder.add(key, value);
                }
                if (semester == null || period.equalsIgnoreCase(semester)) {
                    if (classCode == null ||code.equalsIgnoreCase(classCode)) {
                        builderList.add(new Pair<>(builder, period));
                    }
                }
            }
        }
    }

    protected abstract void saveResult(@NonNull Document document);
    protected abstract Call createCallPreConnect(FormBody.Builder builder);
    public abstract Call makeFinalConnectCall(Document document);

    public LiveData<Resource<Integer>> asLiveData() {
        return result;
    }
}
