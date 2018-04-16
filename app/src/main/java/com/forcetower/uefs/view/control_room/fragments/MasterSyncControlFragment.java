package com.forcetower.uefs.view.control_room.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.SimpleResponse;
import com.forcetower.uefs.service.SyncResponse;
import com.forcetower.uefs.vm.ControlRoomViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 13/03/2018.
 */

public class MasterSyncControlFragment extends Fragment implements Injectable {
    @BindView(R.id.btn_enable)
    Button btnEnable;
    @BindView(R.id.btn_disable)
    Button btnDisable;
    @BindView(R.id.et_login_password)
    EditText etPassword;
    @BindView(R.id.tv_status_message)
    TextView tvStatus;
    @BindView(R.id.tv_connections)
    TextView tvAmount;
    @BindView(R.id.iv_sync_status)
    ImageView ivStatus;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ControlRoomViewModel controlRoomViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master_sync_control, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void setupButtons() {
        btnEnable.setOnClickListener(v -> {
            String pass = etPassword.getText().toString();
            controlRoomViewModel.updateMasterSyncState(true, pass).observe(this, this::stateChangeRequestObserver);
        });

        btnDisable.setOnClickListener(v -> {
            String pass = etPassword.getText().toString();
            controlRoomViewModel.updateMasterSyncState(false, pass).observe(this, this::stateChangeRequestObserver);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        controlRoomViewModel = ViewModelProviders.of(this, viewModelFactory).get(ControlRoomViewModel.class);

        setupButtons();

        if (!controlRoomViewModel.getFragmentCall()) {
            controlRoomViewModel.getCurrentState();
            controlRoomViewModel.setFragmentCall(true);
        }

        controlRoomViewModel.observeCurrentState().observe(this, this::stateObserver);
        controlRoomViewModel.updateMasterSyncState().observe(this, this::stateChangeRequestObserver);
    }

    private void stateChangeRequestObserver(ApiResponse<SimpleResponse> simpleResp) {
        if (simpleResp == null) return;
        if (simpleResp.isSuccessful()) {
            SimpleResponse simple = simpleResp.body;
            //noinspection ConstantConditions
            if (simple.error) {
                Toast.makeText(getContext(), simple.message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), simple.message,Toast.LENGTH_SHORT).show();
                if (simple.isUpdate()) {
                    DrawableCompat.setTint(ivStatus.getDrawable(), ContextCompat.getColor(getContext(), R.color.android_green));
                    tvStatus.setText(R.string.master_sync_enabled);
                } else {
                    DrawableCompat.setTint(ivStatus.getDrawable(), ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
                    tvStatus.setText(R.string.master_sync_disabled);
                }
            }
        } else {
            Toast.makeText(getContext(), R.string.failed_to_connect, Toast.LENGTH_SHORT).show();
        }
    }

    private void stateObserver(ApiResponse<SyncResponse> observeResp) {
        if (observeResp == null) return;
        if (observeResp.isSuccessful()) {
            SyncResponse simple = observeResp.body;
            //noinspection ConstantConditions
            if (simple.isUpdate()) {
                DrawableCompat.setTint(ivStatus.getDrawable(), ContextCompat.getColor(getContext(), R.color.android_green));
                tvStatus.setText(R.string.master_sync_enabled);
            } else {
                DrawableCompat.setTint(ivStatus.getDrawable(), ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
                tvStatus.setText(R.string.master_sync_disabled);
            }

            tvAmount.setText(getString(R.string.master_update_connections, simple.count));
        } else {
            Toast.makeText(getContext(), R.string.failed_to_connect, Toast.LENGTH_SHORT).show();
        }
    }



    //DrawableCompat.setTint(myImageView.getDrawable(), ContextCompat.getColor(context, R.color.another_nice_color));
}
