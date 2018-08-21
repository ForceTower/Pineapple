package com.forcetower.uefs.view.control_room.fragments;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentControlRoomSelectionBinding;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.universe.UniverseActivity;

import javax.inject.Inject;

/**
 * Created by João Paulo on 21/06/2018.
 */
public class ControlRoomSelectionFragment extends Fragment implements Injectable {
    @Inject
    AppExecutors executors;
    @Inject
    ServiceDatabase sDatabase;
    @Inject
    AppDatabase aDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentControlRoomSelectionBinding binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_control_room_selection, container, false);
        binding.masterSync.setOnClickListener(v -> goToMasterSync());
        binding.eventApprove.setOnClickListener(v -> goToEventApproval());
        binding.uneverse.setOnClickListener(v -> goToUneverse());
        binding.logoutUneverse.setOnClickListener(v -> logoutUniverse());
        binding.deleteUnesMessages.setOnClickListener(v -> deleteUnesMessages());
        binding.sendNotification.setOnClickListener(v -> sendNotifications());
        return binding.getRoot();
    }

    private void sendNotifications() {
        requireFragmentManager().beginTransaction()
                .replace(R.id.container, new SendNotificationFragment(), "send_notifications")
                .addToBackStack("send_notification")
                .commitAllowingStateLoss();
    }

    private void deleteUnesMessages() {
        executors.others().execute(() -> aDatabase.messageUNESDao().deleteAll());
    }

    private void logoutUniverse() {
        executors.others().execute(() -> sDatabase.accessTokenDao().deleteAll());
    }

    private void goToUneverse() {
        UniverseActivity.startActivity(requireContext());
    }

    private void goToMasterSync() {
        requireFragmentManager().beginTransaction()
                .replace(R.id.container, new MasterSyncControlFragment(), "update_control")
                .addToBackStack("update_control")
                .commitAllowingStateLoss();
    }

    private void goToEventApproval() {
        requireFragmentManager().beginTransaction()
                .replace(R.id.container, new EventApprovalFragment(), "event_approval")
                .addToBackStack("event_approval")
                .commitAllowingStateLoss();
    }
}
