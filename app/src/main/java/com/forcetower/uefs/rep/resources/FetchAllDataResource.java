package com.forcetower.uefs.rep.resources;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.sgrs.SagresResponse;
import com.forcetower.uefs.util.network.LiveDataCallAdapter;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import okhttp3.Call;
import timber.log.Timber;

/**
 * Created by João Paulo on 06/03/2018.
 */

public abstract class FetchAllDataResource {
    private final AppExecutors executors;
    private final MediatorLiveData<Resource<Integer>> result;

    @MainThread
    public FetchAllDataResource(AppExecutors executors) {
        this.executors = executors;
        result = new MediatorLiveData<>();
        Timber.d("Fetch all data called");

        executors.diskIO().execute(() -> {
            result.postValue(Resource.loading(R.string.connecting));
            fetchFromSagres();
        });
    }

    private void fetchFromSagres() {
        Call call = createCall();
        LiveData<SagresResponse> sgrRspLive = LiveDataCallAdapter.adapt(call);

        result.addSource(sgrRspLive, response -> {
            result.removeSource(sgrRspLive);
            //noinspection ConstantConditions
            if (response.isSuccessful() && response.getDocument() != null) {
                Document document = response.getDocument();
                if (isConnected(document)) {
                    if (needApproval(document)) {
                        Timber.d("Need approval...");
                        setValue(Resource.loading(R.string.accessing_portal));
                        LiveData<Resource<Document>> docLive = approve(response);
                        result.addSource(docLive, documentResource -> {
                            result.removeSource(docLive);
                            //noinspection ConstantConditions
                            if (documentResource.status == Status.SUCCESS) {
                                Timber.d("Success on approval!");
                                executors.diskIO().execute(() -> {
                                    initialPage(documentResource.data);
                                    executors.mainThread().execute(this::navigateToStudentPage);
                                });

                                //navigateToStudentPage();
                            } else {
                                Timber.d("Failed on approval...");
                            }
                        });
                        //navigateToStudentPage();
                    } else {
                        Timber.d("Don't need approval!");
                        executors.diskIO().execute(() -> {
                            initialPage(document);
                            executors.mainThread().execute(this::navigateToStudentPage);
                        });
                    }
                } else {
                    onFetchFailed();
                    Timber.d("Not connected");
                    setValue(Resource.error("Failed to login", 401, R.string.invalid_login));
                }
            } else {
                onFetchFailed();
                setValue(Resource.error(response.getMessage(), response.getCode(), R.string.failed_to_connect));
            }
        });
    }

    protected abstract void initialPage(Document data);

    private LiveData<Resource<Document>> approve(SagresResponse sgrResponse) {
        MediatorLiveData<Resource<Document>> docLive = new MediatorLiveData<>();

        Call call = approvalCall(sgrResponse);
        LiveData<SagresResponse> sgrRspLive = LiveDataCallAdapter.adapt(call);
        docLive.addSource(sgrRspLive, response -> {
            docLive.removeSource(sgrRspLive);
            //noinspection ConstantConditions
            if (response.isSuccessful()) {
                Timber.d("Approval success");
                docLive.postValue(Resource.success(response.getDocument()));
            } else {
                Timber.d("Approval failed");
                docLive.postValue(Resource.error(response.getMessage(), response.getCode(), (Throwable)null));
            }
        });

        return docLive;

    }

    private void navigateToStudentPage() {
        result.postValue(Resource.loading(R.string.going_to_student_page));
        Call call = createStudentPageCall();
        LiveData<SagresResponse> sgrResp = LiveDataCallAdapter.adapt(call);

        result.addSource(sgrResp, response -> {
            result.removeSource(sgrResp);

            //noinspection ConstantConditions
            if (response.isSuccessful()) {
                Document document = response.getDocument();
                executors.diskIO().execute(() -> {
                    result.postValue(Resource.loading(R.string.processing_information));
                    if (document != null) {
                        saveResult(document);
                        executors.mainThread().execute(() -> setValue(Resource.success(R.string.completed)));
                    } else {
                        executors.mainThread().execute(() ->
                                setValue(Resource.error("Response is fine, but document is null", 500, R.string.failed_to_connect)));
                    }

                });

            } else {
                result.postValue(Resource.error(response.getMessage(), response.getCode(), R.string.failed_to_connect));
            }
        });
    }

    private boolean needApproval(Document document) {
        Element approval = document.selectFirst("div[class=\"acesso-externo-pagina-login\"]");
        if (approval != null) return true;

        approval = document.selectFirst("input[value=\"Acessar o SAGRES Portal\"]");
        return approval != null;

    }

    private boolean isConnected(Document document) {
        Element element = document.selectFirst("div[class=\"externo-erro\"]");
        if (element != null) {
            if (element.text().length() != 0) {
                Timber.d("Login failed - Invalid Credentials");
                return false;
            } else {
                Timber.d("Login failed - Length is different now");
                return false;
            }
        } else {
            Timber.d("Correct Login");
            return true;
        }
    }

    public LiveData<Resource<Integer>> asLiveData() {
        return result;
    }

    @MainThread
    private void setValue(Resource<Integer> b) {
        Resource<Integer> a = result.getValue();
        if (!((a == b) || (a != null && a.equals(b)))) {
            result.setValue(b);
        }
    }

    public void onFetchFailed() {}
    public abstract Call createCall();
    public abstract Call approvalCall(SagresResponse sgrResponse);
    public abstract Call createStudentPageCall();
    public abstract void saveResult(@NonNull Document document);

}
