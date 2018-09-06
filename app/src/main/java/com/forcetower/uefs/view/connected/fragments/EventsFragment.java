package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.forcetower.uefs.R;
import com.forcetower.uefs.anim.ChangeBoundsTransition;
import com.forcetower.uefs.databinding.FragmentEventsBinding;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.view.connected.adapters.EventAdapter;
import com.forcetower.uefs.view.event.EventDetailsActivity;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.base.ProfileViewModel;
import com.forcetower.uefs.vm.service.EventsViewModel;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.forcetower.uefs.util.SupportUtils.getGravityCompat;

/**
 * Created by João Paulo on 15/06/2018.
 */
public class EventsFragment extends Fragment implements Injectable {
    @Inject
    UEFSViewModelFactory viewModelFactory;
    @Inject
    NavigationController navigation;

    private FragmentEventsBinding binding;
    private EventAdapter adapter;
    private ActivityController controller;
    private EventsViewModel eventsViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_events, container, false);
        controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.nav_title_events);

        binding.btnCreateEvent.setOnClickListener(v -> onCreateEvent());
        prepareRecyclerView();
        return binding.getRoot();
    }

    private void onCreateEvent() {
        if (VersionUtils.isLollipop()) {
            setExitTransition(new Slide(getGravityCompat(requireContext(), Gravity.START)));
            setAllowEnterTransitionOverlap(false);
            setAllowReturnTransitionOverlap(false);
        }
        navigation.navigateToCreateEvent(requireContext());
    }

    private void prepareRecyclerView() {
        adapter = new EventAdapter();
        binding.recyclerEvents.setNestedScrollingEnabled(false);
        binding.recyclerEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerEvents.setAdapter(adapter);
        binding.recyclerEvents.setItemAnimator(new DefaultItemAnimator());

        adapter.setListener((event, view, position) -> {
            Intent intent = EventDetailsActivity.startActivity(requireContext(), event.getUuid());
            Bundle options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    view,
                    "event_image_target_transition"
            ).toBundle();
            if (VersionUtils.isLollipop()) {
                Window window = requireActivity().getWindow();
                window.setExitTransition(new Explode());
                window.setSharedElementEnterTransition(new ChangeBoundsTransition());
            }
            startActivity(intent, options);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eventsViewModel = ViewModelProviders.of(this, viewModelFactory).get(EventsViewModel.class);
        ProfileViewModel profileViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        profileViewModel.getProfile().observe(this, this::onProfileUpdate);
        eventsViewModel.getEvents().observe(this, this::onEventsUpdate);
    }

    private void onProfileUpdate(Profile profile) {
        int reference = 0;
        if (profile != null) {
            reference = profile.getCourseReference();
        }

        eventsViewModel.setCoursePointer(reference);
    }

    private void onEventsUpdate(Resource<List<Event>> eventsRes) {
        if (eventsRes.data != null) {
            if (eventsRes.data.isEmpty()) {
                binding.recyclerEvents.setVisibility(View.GONE);
                binding.textNoEvents.setVisibility(View.VISIBLE);
            } else {
                adapter.setEvents(eventsRes.data);
                binding.recyclerEvents.setVisibility(View.VISIBLE);
                binding.textNoEvents.setVisibility(View.GONE);
            }
        }

        switch (eventsRes.status) {
            case ERROR:
                Timber.d("Error loading from network");
                Timber.d("Code: " + eventsRes.code);
                Timber.d("Message: " + eventsRes.message);
                break;
            case LOADING:
                Timber.d("Loading from network");
                break;
            case SUCCESS:
                Timber.d("Success loading data from network");
                break;
        }
    }
}
