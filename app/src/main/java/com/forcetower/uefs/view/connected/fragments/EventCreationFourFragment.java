package com.forcetower.uefs.view.connected.fragments;

import android.app.Service;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentEventCreationFourBinding;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.db_service.entity.Course;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.db_service.helper.ImGurDataObject;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.service.EventsViewModel;
import com.forcetower.uefs.vm.service.ServiceGeneralViewModel;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.forcetower.uefs.util.SupportUtils.getGravityCompat;
import static com.forcetower.uefs.util.WordUtils.validString;

/**
 * Created by João Paulo on 16/06/2018.
 */
public class EventCreationFourFragment extends Fragment implements Injectable {
    private static final int REQUEST_SELECT_EVENT_PICTURE = 8000;

    @Inject
    UEFSViewModelFactory viewModelFactory;
    @Inject
    NavigationController controller;
    @Inject
    AppDatabase database;

    private FragmentEventCreationFourBinding binding;
    private EventsViewModel viewModel;
    private boolean canChange = true;
    private ArrayAdapter<Course> courseAdapter;
    private List<Course> courses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_creation_four, container, false);
        binding.btnContinue.setOnClickListener(v -> onNextEvent());
        binding.eventImage.setOnClickListener(v -> changeImageEvent());
        binding.btnTryAgain.setOnClickListener(v -> onRetryUploadEvent());
        setupSpinner();
        return binding.getRoot();
    }

    private void setupSpinner() {
        courseAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line);
        binding.spiCourse.setAdapter(courseAdapter);
        binding.spiCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Course selected = courses.get(position);
                Timber.d("Selected course: " + selected.getName());
                viewModel.getCurrentEvent().setCoursePointer(selected.getServiceId());
                if (selected.getServiceId() != 0)
                    Toast.makeText(requireContext(), R.string.this_may_not_be_supported_yet, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(EventsViewModel.class);
        viewModel.getBlurImage().observe(this, this::onBlurImageUpdate);
        viewModel.getUploadImGur().observe(this, this::onEventImageUploaded);

        database.accessDao().getAccess().observe(this, this::onAccessChange);
        database.profileDao().getProfile().observe(this, this::onProfileChange);

        ServiceGeneralViewModel serviceViewModel = ViewModelProviders.of(this, viewModelFactory).get(ServiceGeneralViewModel.class);
        serviceViewModel.getCourses().observe(this, this::onCourseChange);
    }

    private void onCourseChange(Resource<List<Course>> coursesRes) {
        List<Course> courses = coursesRes.data != null ? coursesRes.data : new ArrayList<>();
        courses.add(0, Course.getUndefined(requireContext()));

        courseAdapter.clear();
        courseAdapter.addAll(courses);
        this.courses = courses;
    }

    private void onProfileChange(Profile profile) {
        if (profile != null) {
            viewModel.getCurrentEvent().setCreatorName(profile.getName());
        }
    }

    private void onAccessChange(Access access) {
        if (access != null) {
            viewModel.getCurrentEvent().setCreatorUsername(access.getUsername());
        }
    }

    private void onBlurImageUpdate(Bitmap bitmap) {
        if (bitmap != null && !canChange) {
            binding.eventImage.setImageBitmap(bitmap);
        }
    }

    private void onEventImageUploaded(Resource<ImGurDataObject> resource) {
        switch (resource.status) {
            case ERROR:
                binding.btnTryAgain.setVisibility(View.VISIBLE);
                binding.loadingImage.setVisibility(View.GONE);
                canChange = true;
                break;
            case LOADING:
                binding.btnTryAgain.setVisibility(View.GONE);
                binding.loadingImage.setVisibility(View.VISIBLE);
                canChange = false;
                break;
            case SUCCESS:
                binding.btnTryAgain.setVisibility(View.GONE);
                binding.loadingImage.setVisibility(View.GONE);
                canChange = true;
                if (resource.data != null && resource.data.getLink() != null && !resource.data.getLink().isEmpty()) {
                    viewModel.getCurrentEvent().setImageUrl(resource.data.getLink());
                    Timber.d("Image delete Hash: " + resource.data.getDeleteHash());
                    viewModel.getCurrentEvent().setDeleteHash(resource.data.getDeleteHash());
                    refreshInterface(viewModel.getCurrentEvent());
                } else {
                    Timber.d("Resource Data: " + resource.data);
                    Toast.makeText(requireContext(), R.string.upload_failed, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInterface(viewModel.getCurrentEvent());
    }

    private void refreshInterface(Event event) {
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            Picasso.with(requireContext()).load(event.getImageUrl()).into(binding.eventImage);
        } else {
            Timber.d("Invalid image for now. Image: " + event.getImageUrl());
        }
    }

    private void changeImageEvent() {
        if (!canChange) {
            Toast.makeText(requireContext(), R.string.wait_until_image_is_processed, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_SELECT_EVENT_PICTURE);
    }

    private void onRetryUploadEvent() {
        if (viewModel.getCurrentUri() != null)
            onImageUriReceived(viewModel.getCurrentUri());
        else {
            Timber.e("Uri is null");
            Toast.makeText(requireContext(), R.string.try_again_is_impossible, Toast.LENGTH_SHORT).show();
            binding.btnTryAgain.setVisibility(View.GONE);
        }
    }

    private void onNextEvent() {
        if (validString(viewModel.getCurrentEvent().getImageUrl())) {
            if (VersionUtils.isLollipop()) {
                setExitTransition(new Slide(getGravityCompat(requireContext(), Gravity.START)));
                setAllowEnterTransitionOverlap(false);
                setAllowReturnTransitionOverlap(false);
            }

            Timber.d("Valid Event");

            controller.navigateToCreateEventPreview(requireContext());
        }
    }

    private void onImageUriReceived(Uri uri) {
        try {
            InputStream imageStream = requireActivity().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            viewModel.blurImage(requireContext(), bitmap);
            viewModel.uploadImageToImGur(bitmap, uri);
        } catch (Exception e) {
            Crashlytics.logException(e);
            Toast.makeText(requireContext(), R.string.error_creating_image, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_EVENT_PICTURE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();

                CropImage.activity(selectedImage)
                        .setFixAspectRatio(true)
                        .setAspectRatio(16, 9)
                        .setActivityTitle(getString(R.string.cut_event_image))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(requireContext(), this);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri imageUri = result.getUri();
                onImageUriReceived(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Crashlytics.logException(error);
                Toast.makeText(requireContext(), R.string.error_creating_image, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
