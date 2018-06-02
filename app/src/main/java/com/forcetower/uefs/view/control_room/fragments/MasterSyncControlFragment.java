package com.forcetower.uefs.view.control_room.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentMasterSyncControlBinding;
import com.forcetower.uefs.db_service.entity.UpdateStatus;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.vm.admin.ControlRoomViewModel;

import javax.inject.Inject;

/**
 * Created by João Paulo on 13/03/2018.
 */

public class MasterSyncControlFragment extends Fragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ControlRoomViewModel controlRoomViewModel;
    private FragmentMasterSyncControlBinding binding;

    private boolean alarm;
    private boolean manager;
    private boolean worker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_sync_control, container, false);
        return binding.getRoot();
    }

    private void setupButtons() {
        binding.btnToggleAlarm.setOnClickListener(v ->
                controlRoomViewModel.updateMasterSyncState(manager, !alarm).observe(this, this::stateChangeRequestObserver));

        binding.btnToggleManager.setOnClickListener(v ->
                controlRoomViewModel.updateMasterSyncState(!manager, alarm).observe(this, this::stateChangeRequestObserver));

        binding.btnToggleWorker.setOnClickListener(v ->
                controlRoomViewModel.updateWorkerSyncState(!worker).observe(this, this::stateChangeRequestObserver));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        controlRoomViewModel = ViewModelProviders.of(this, viewModelFactory).get(ControlRoomViewModel.class);

        if (savedInstanceState != null) {
            alarm = savedInstanceState.getBoolean("alarm", false);
            manager = savedInstanceState.getBoolean("manager", false);
            worker = savedInstanceState.getBoolean("worker", false);
            updateAlarm(alarm);
            updateManager(manager);
            updateWorker(worker);
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
            updateWorker(simple.getData().isWorker());
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
            updateWorker(simple.isWorker());
            binding.tvConnections.setText(getString(R.string.master_update_connections, simple.getCount()));
        } else {
            Toast.makeText(getContext(), R.string.failed_to_connect, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateManager(boolean manager) {
        this.manager = manager;
        if (manager) {
            int color = ContextCompat.getColor(requireContext(), R.color.android_green);
            DrawableCompat.setTint(binding.ivManagerStatus.getDrawable(), color);
            binding.btnToggleManager.setText(R.string.disable);
        } else {
            int color = ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark);
            DrawableCompat.setTint(binding.ivManagerStatus.getDrawable(), color);
            binding.btnToggleManager.setText(R.string.enable);
        }
    }

    private void updateAlarm(boolean alarm) {
        this.alarm = alarm;
        if (alarm) {
            int color = ContextCompat.getColor(requireContext(), R.color.android_green);
            DrawableCompat.setTint(binding.ivAlarmStatus.getDrawable(), color);
            binding.btnToggleAlarm.setText(R.string.disable);
        } else {
            int color = ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark);
            DrawableCompat.setTint(binding.ivAlarmStatus.getDrawable(), color);
            binding.btnToggleAlarm.setText(R.string.enable);
        }
    }

    private void updateWorker(boolean worker) {
        this.worker = worker;
        if (worker) {
            int color = ContextCompat.getColor(requireContext(), R.color.android_green);
            DrawableCompat.setTint(binding.ivWorkerStatus.getDrawable(), color);
            binding.btnToggleWorker.setText(R.string.disable);
        } else {
            int color = ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark);
            DrawableCompat.setTint(binding.ivWorkerStatus.getDrawable(), color);
            binding.btnToggleWorker.setText(R.string.enable);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("alarm", alarm);
        outState.putBoolean("manager", manager);
        outState.putBoolean("worker", worker);
    }
}
