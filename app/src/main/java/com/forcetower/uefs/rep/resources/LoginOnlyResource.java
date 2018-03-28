package com.forcetower.uefs.rep.resources;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;

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
 * Created by João Paulo on 28/03/2018.
 */
public abstract class LoginOnlyResource {
    private final AppExecutors executors;
    private final MediatorLiveData<Resource<Integer>> result;

    @MainThread
    public LoginOnlyResource(AppExecutors executors) {
        this.executors = executors;
        result = new MediatorLiveData<>();

        loginOnSagres();
    }

    private void loginOnSagres() {
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
                                setValue(Resource.success(R.string.completed));
                            } else {
                                Timber.d("Failed on approval...");
                                setValue(Resource.error("Failed on approval", 500, R.string.failed_to_connect));
                            }
                        });
                    } else {
                        Timber.d("Don't need approval!");
                        setValue(Resource.success(R.string.completed));
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
                docLive.postValue(Resource.error(response.getMessage(), response.getCode(), null));
            }
        });

        return docLive;

    }

    @MainThread
    private void setValue(Resource<Integer> b) {
        Resource<Integer> a = result.getValue();
        if (!((a == b) || (a != null && a.equals(b)))) {
            result.setValue(b);
        }
    }

    public LiveData<Resource<Integer>> asLiveData() {
        return result;
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

    public void onFetchFailed() {}
    public abstract Call createCall();
    public abstract Call approvalCall(SagresResponse sgrResponse);
}
