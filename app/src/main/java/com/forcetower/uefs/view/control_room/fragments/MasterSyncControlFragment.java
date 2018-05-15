package com.forcetower.uefs.view.control_room.fragments;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db_service.entity.UpdateStatus;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.vm.admin.ControlRoomViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 13/03/2018.
 */

public class MasterSyncControlFragment extends Fragment implements Injectable {
    @BindView(R.id.btn_toggle_manager)
    Button btnManager;
    @BindView(R.id.btn_toggle_alarm)
    Button btnAlarm;
    @BindView(R.id.iv_manager_status)
    ImageView ivManagerStatus;
    @BindView(R.id.iv_alarm_status)
    ImageView ivAlarmStatus;
    @BindView(R.id.tv_connections)
    TextView tvAmount;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ControlRoomViewModel controlRoomViewModel;

    private boolean alarm;
    private boolean manager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master_sync_control, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void setupButtons() {
        btnAlarm.setOnClickListener(v -> {
            controlRoomViewModel.updateMasterSyncState(manager, !alarm).observe(this, this::stateChangeRequestObserver);
        });

        btnManager.setOnClickListener(v -> {
            controlRoomViewModel.updateMasterSyncState(!manager, alarm).observe(this, this::stateChangeRequestObserver);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        controlRoomViewModel = ViewModelProviders.of(this, viewModelFactory).get(ControlRoomViewModel.class);

        if (savedInstanceState != null) {
            alarm = savedInstanceState.getBoolean("alarm", false);
            manager = savedInstanceState.getBoolean("manager", false);
            updateAlarm(alarm);
            updateManager(manager);
        }

        setupButtons();

        if (!controlRoomViewModel.getFragmentCall()) {
            controlRoomViewModel.getCurrentState();
            controlRoomViewModel.setFragmentCall(true);
        }

        controlRoomViewModel.observeCurrentState().observe(this, this::stateObserver);
        controlRoomViewModel.updateMasterSyncState().observe(this, this::stateChangeRequestObserver);
    }

    private void stateChangeRequestObserver(ApiResponse<ActionResult<UpdateStatus>> simpleResp) {
        if (simpleResp == null) return;
        if (simpleResp.isSuccessful()) {
            ActionResult<UpdateStatus> simple = simpleResp.body;
            //noinspection ConstantConditions
            Toast.makeText(getContext(), simple.getMessage(), Toast.LENGTH_SHORT).show();
            updateManager(simple.getData().isManager());
            updateAlarm(simple.getData().isAlarm());
        } else {
            if (simpleResp.actionError != null && simpleResp.actionError.getMessage() != null) {
                Toast.makeText(getContext(), simpleResp.actionError.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.failed_to_connect, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stateObserver(ApiResponse<UpdateStatus> updateStsResp) {
        if (updateStsResp == null) return;
        if (updateStsResp.isSuccessful()) {
            UpdateStatus simple = updateStsResp.body;
            //noinspection ConstantConditions
            updateManager(simple.isManager());
            updateAlarm(simple.isAlarm());
            tvAmount.setText(getString(R.string.master_update_connections, simple.getCount()));
        } else {
            Toast.makeText(getContext(), R.string.failed_to_connect, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateManager(boolean manager) {
        this.manager = manager;
        if (manager) {
            int color = ContextCompat.getColor(requireContext(), R.color.android_green);
            DrawableCompat.setTint(ivManagerStatus.getDrawable(), color);
            btnManager.setText(R.string.disable);
        } else {
            int color = ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark);
            DrawableCompat.setTint(ivManagerStatus.getDrawable(), color);
            btnManager.setText(R.string.enable);
        }
    }

    private void updateAlarm(boolean alarm) {
        this.alarm = alarm;
        if (alarm) {
            int color = ContextCompat.getColor(requireContext(), R.color.android_green);
            DrawableCompat.setTint(ivAlarmStatus.getDrawable(), color);
            btnAlarm.setText(R.string.disable);
        } else {
            int color = ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark);
            DrawableCompat.setTint(ivAlarmStatus.getDrawable(), color);
            btnAlarm.setText(R.string.enable);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("alarm", alarm);
        outState.putBoolean("manager", manager);
    }
}
