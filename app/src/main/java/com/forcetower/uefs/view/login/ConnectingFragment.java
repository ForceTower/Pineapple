package com.forcetower.uefs.view.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.exception.LoginException;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.managers.SagresLoginManager;
import com.forcetower.uefs.services.tasks.MigrateToLocalDatabaseTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 24/12/2017.
 */

public class ConnectingFragment extends Fragment {
    @BindView(R.id.iv_app_logo)
    ImageView imageView;
    @BindView(R.id.pb_connect_progress)
    ProgressBar loadProgress;
    @BindView(R.id.tv_connect_info)
    TextView connectInfo;

    private LoginViewCallback callback;

    public ConnectingFragment(){}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (LoginViewCallback) context;
        } catch (ClassCastException e) {
            Log.e(APP_TAG, "onAttach: must implement LoginViewCallback");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connecting, container, false);
        ButterKnife.bind(this, view);

        AlphaAnimation fade = new AlphaAnimation(0.65f, 1f);
        fade.setRepeatMode(Animation.REVERSE);
        fade.setDuration(750);
        fade.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(fade);
        if (Utils.isLollipop()) imageView.setElevation(5);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle login = getArguments();
        SagresPortalSDK.addLoginListener(this::receiveUpdate);
        if (login == null) {
            //callback.onLoginFailed(new NullPointerException("Bundle is null"));
            Log.i(APP_TAG, "Login bundle is null... Activity will only end by async interference");
            return;
        }
        String username = login.getString("username");
        String password = login.getString("password");
        connectToPortal(username, password);
    }

    @Override
    public void onPause() {
        super.onPause();
        SagresPortalSDK.removeLoginListener(this::receiveUpdate);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void connectToPortal(final String username, final String password) {
        loadProgress.setProgress(0);
        SagresLoginManager.getInstance().login(username, password, new SagresLoginManager.SagresLoginCallback() {
            @Override
            public void onSuccess() {
                Log.i(APP_TAG, "LoginActivity::ParseSuccess()");
                //new Thread(()->{
                    try {
                        new MigrateToLocalDatabaseTask(((UEFSApplication) getActivity().getApplicationContext()).getApplicationComponent()).execute().get();
                    } catch (Exception e) {e.printStackTrace();}
                    //onLoginSuccess();
                //}).start();
                callback.onLoginSuccess();
            }

            @Override
            public void onFailure(boolean failedConnection) {
                if (failedConnection) {
                    callback.onLoginFailed(new LoginException(LoginException.CONNECTION_ERROR));
                } else {
                    callback.onLoginFailed(new LoginException(LoginException.INVALID_LOGIN));
                }
            }

            @Override
            public void onLoginSuccess() {
                receiveUpdate(0, null);
            }
        });
    }

    private void receiveUpdate(int idx, String str) {
        if (getActivity() != null && isVisible()) {
            getActivity().runOnUiThread(()->{
                if (idx == 1) {
                    connectInfo.setText(getString(R.string.welcome, str));
                    setProgress(25);
                } else if (idx == 3) {
                    setProgress(35);
                    connectInfo.setText(getString(R.string.retrying_connection, str));
                } else if (idx == 4) {
                    setProgress(60);
                    connectInfo.setText(R.string.processing_info);
                } else if (idx == 5) {
                    setProgress(45);
                    connectInfo.setText(R.string.failed_connect_to_portal);
                } else if (idx == 6) {
                    setProgress(60);
                    connectInfo.setText(R.string.prepare_to_fetch_grades);
                } else if (idx == 7) {
                    setProgress(85);
                    connectInfo.setText(R.string.processing_info);
                } else if (idx == 10) {
                    setProgress(100);
                    connectInfo.setText(R.string.finished);
                }
            });
        }
    }

    @MainThread
    private void setProgress(int progress) {
        if (Utils.isNougat()) loadProgress.setProgress(progress, true);
        else loadProgress.setProgress(progress);
    }
}
