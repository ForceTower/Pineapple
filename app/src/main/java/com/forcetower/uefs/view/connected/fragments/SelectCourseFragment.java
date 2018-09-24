package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentSelectCourseBinding;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db_service.entity.Course;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.view.SimpleDialog;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.view.connected.adapters.CourseAdapter;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.base.ProfileViewModel;
import com.forcetower.uefs.vm.service.ServiceGeneralViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by João Paulo on 20/06/2018.
 */
public class SelectCourseFragment extends Fragment implements Injectable {
    @Inject
    UEFSViewModelFactory viewModelFactory;
    @Inject
    NavigationController navigationController;
    @Inject
    SharedPreferences preferences;

    private FragmentSelectCourseBinding binding;
    private CourseAdapter adapter;
    private Profile profile;
    private ProfileViewModel profileViewModel;
    private ActivityController controller;
    private Access access;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_course, container, false);
        binding.btnHelp.setOnClickListener(v -> onHelpClick());
        if (controller.getTabLayout() != null) controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_select_course);
        prepareRecyclerView();

        return binding.getRoot();
    }

    private void prepareRecyclerView() {
        adapter = new CourseAdapter(course -> {
            SimpleDialog dialog = SimpleDialog.newDialog(
                    1,
                    course.getName(),
                    getString(R.string.is_this_your_course),
                    new int[] {R.string.yes, R.string.no});

            dialog.setListener((id, which) -> {
                Timber.d("Clicked %d", which);
                switch (which) {
                    case -1:
                        if (profile != null && access != null && profile.getCourse() != null) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("courses");
                            reference.child(profile.getCourseFixed()).child(access.getUsernameFixed()).removeValue()
                                    .addOnSuccessListener(aVoid -> Timber.d("Finished"));
                        }
                        profileViewModel.setProfileCourse(course.getName(), course.getServiceId());
                        preferences.edit()
                                .putInt("user_course_int", course.getServiceId())
                                .putString("user_course_str", course.getName()).apply();
                        navigationController.back();
                        break;
                }
            });

            dialog.openDialog(getChildFragmentManager());
        });
        binding.recyclerCourses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCourses.setAdapter(adapter);
        binding.recyclerCourses.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerCourses.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ServiceGeneralViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(ServiceGeneralViewModel.class);
        viewModel.getCourses().observe(this, this::onCoursesUpdate);
        profileViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        profileViewModel.getProfile().observe(this, this::onProfileUpdate);
        profileViewModel.getAccess().observe(this, this::onAccessUpdate);
    }

    private void onAccessUpdate(Access access) {
        if (access != null)
            this.access = access;
    }

    private void onProfileUpdate(Profile profile) {
        if (profile != null)
            this.profile = profile;
    }

    private void onCoursesUpdate(Resource<List<Course>> listResource) {
        switch (listResource.status) {
            case SUCCESS:
                Timber.d("Success");
                break;
            case LOADING:
                Timber.d("Loading");
                break;
            case ERROR:
                Timber.d("Error");
                if (listResource.data == null || listResource.data.isEmpty()) {
                    binding.tvLoading.setText(R.string.failed_loading_courses);
                }
                break;
        }

        if (listResource.data == null || listResource.data.isEmpty()) {
            binding.tvLoading.setVisibility(View.VISIBLE);
            binding.recyclerCourses.setVisibility(View.GONE);
        } else {
            adapter.setCourses(listResource.data);
            binding.tvLoading.setVisibility(View.GONE);
            binding.recyclerCourses.setVisibility(View.VISIBLE);
        }

    }

    private void onHelpClick() {
        SimpleDialog dialog = SimpleDialog.newDialog(
                0,
                getString(R.string.course_selection),
                getString(R.string.course_selection_help),
                new int[] {R.string.ok});

        dialog.setListener((id, which) -> {
            Timber.d("Clicked ok");
        });

        dialog.openDialog(getChildFragmentManager());
    }
}
