package com.forcetower.uefs.view.control_room.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentControlRoomSelectionBinding;
import com.forcetower.uefs.view.universe.UniverseActivity;

/**
 * Created by João Paulo on 21/06/2018.
 */
public class ControlRoomSelectionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentControlRoomSelectionBinding binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_control_room_selection, container, false);
        binding.masterSync.setOnClickListener(v -> goToMasterSync());
        binding.eventApprove.setOnClickListener(v -> goToEventApproval());
        binding.uneverse.setOnClickListener(v -> goToUneverse());
        return binding.getRoot();
    }

    private void goToUneverse() {
        UniverseActivity.startActivity(requireContext());
    }

    private void goToEventApproval() {
        requireFragmentManager().beginTransaction()
                .replace(R.id.container, new MasterSyncControlFragment(), "update_control")
                .addToBackStack("update_control")
                .commitAllowingStateLoss();
    }

    private void goToMasterSync() {
        requireFragmentManager().beginTransaction()
                .replace(R.id.container, new EventApprovalFragment(), "event_approval")
                .addToBackStack("event_approval")
                .commitAllowingStateLoss();
    }
}
