package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.forcetower.uefs.BuildConfig;
import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentProfileBinding;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db.entity.Semester;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.DateUtils;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.view.control_room.ControlRoomActivity;
import com.forcetower.uefs.vm.base.DisciplinesViewModel;
import com.forcetower.uefs.vm.base.ProfileViewModel;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static android.os.Looper.getMainLooper;
import static com.forcetower.uefs.view.connected.LoggedActivity.BACKGROUND_IMAGE;

/**
 * Created by João Paulo on 08/03/2018.
 */

public class ProfileFragment extends Fragment implements Injectable {
    public static final int REQUEST_SELECT_PROFILE_PICTURE = 5000;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;

    private ProfileViewModel profileViewModel;
    private SharedPreferences sharedPreferences;

    private ActivityController controller;
    private FragmentProfileBinding binding;

    private double scoreCalc = -1;
    private boolean alternateScore = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_profile);

        binding.ivImgProfile.setOnLongClickListener(v -> onProfileImageLongClick());
        binding.ivImgProfile.setOnClickListener(v -> selectPicture());
        binding.ivImgPlaceholder.setOnClickListener(v -> selectPicture());
        binding.incCardUpdateCtrl.cvUpdateControl.setOnClickListener(v -> goToUpdateControl());
        binding.incCardCourse.cvSelectCourse.setOnClickListener(v -> changeCourse());

        binding.tvLastUpdateAttempt.setOnLongClickListener(v -> {
            navigationController.navigateToSyncRegistry();
            return true;
        });

        binding.tvLastUpdate.setOnLongClickListener(v -> {
            navigationController.navigateToSyncRegistry();
            return true;
        });

        if (BuildConfig.DEBUG) enablePrivateContent();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (sharedPreferences.getBoolean("show_score", false)) {
            binding.tvStdScore.setVisibility(View.VISIBLE);
        }

        if (!sharedPreferences.getBoolean("feature_discovered_profile_image", false)) {
            new Handler(getMainLooper()).postDelayed(this::discoverProfilePicture, 500);
        }

        String backgroundImage = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString(BACKGROUND_IMAGE, Constants.BACKGROUND_IMAGE_DEFAULT);
        Picasso.with(requireContext()).load(backgroundImage).into(binding.ivBackground, new Callback() {
            @Override
            public void onSuccess() { binding.vwBgAlpha.setVisibility(View.VISIBLE); }
            @Override
            public void onError() { binding.vwBgAlpha.setVisibility(View.INVISIBLE); }
        });

        return binding.getRoot();
    }

    private void changeCourse() {
        navigationController.navigateToSelectCourse();
    }

    public void selectPicture() {
        Timber.d("Fired on click listener");
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_SELECT_PROFILE_PICTURE);
    }

    public boolean onProfileImageLongClick() {
        AnimUtils.fadeOut(requireContext(), binding.ivImgProfile);
        AnimUtils.fadeIn(requireContext(), binding.ivImgPlaceholder);
        profileViewModel.saveProfileImageBitmap(null);
        controller.onProfileImageChanged(null);
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        profileViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        profileViewModel.getProfile().observe(this, this::onReceiveProfile);
        profileViewModel.getSemesters().observe(this, this::onReceiveSemesters);
        profileViewModel.getAccess().observe(this, this::onReceiveAccess);
        profileViewModel.getProfileImage().observe(this, this::onReceiveProfileImage);

        DisciplinesViewModel disciplinesViewModel = ViewModelProviders.of(this, viewModelFactory).get(DisciplinesViewModel.class);
        disciplinesViewModel.getScore().observe(this, this::onScoreCalculated);
    }

    private void onScoreCalculated(Double value) {
        if (value != null) {
            this.scoreCalc = value;
            if (scoreCalc >= 0 && alternateScore) {
                binding.tvStdScore.setText(getString(R.string.calculated_score, scoreCalc));
            }
        }
    }

    private void onReceiveProfileImage(Bitmap bitmap) {
        controller.onProfileImageChanged(bitmap);

        if (bitmap == null) {
            Timber.d("No image set so far");
            AnimUtils.fadeIn(requireContext(), binding.ivImgPlaceholder);
            AnimUtils.fadeOut(requireContext(), binding.ivImgProfile);
            return;
        }

        binding.ivImgProfile.setImageBitmap(bitmap);
        AnimUtils.fadeIn(requireContext(), binding.ivImgProfile);
        AnimUtils.fadeOut(requireContext(), binding.ivImgPlaceholder);
    }

    private void onReceiveAccess(Access access) {
        if (access == null) return;

        if (access.getUsername().equalsIgnoreCase("jpssena")) {
            enablePrivateContent();
        }
    }

    private void enablePrivateContent() {
        binding.incCardUpdateCtrl.cvUpdateControl.setVisibility(View.VISIBLE);
    }

    private void onReceiveProfile(Profile profile) {
        if (profile == null) return;

        binding.tvStdName.setText(profile.getName());

        if (profile.getScore() >= 0) {
            binding.tvStdScore.setText(getString(R.string.student_score, profile.getScore()));
        } else {
            alternateScore = true;
            if (scoreCalc >= 0) {
                binding.tvStdScore.setText(getString(R.string.calculated_score, scoreCalc));
            } else {
                binding.tvStdScore.setText(R.string.no_score_message);
            }
        }

        Timber.d("Last Sync: %s", profile.getLastSync());
        String date = DateUtils.convertTime(profile.getLastSync());
        String attempt = DateUtils.convertTime(profile.getLastSyncAttempt());
        binding.tvLastUpdate.setText(getString(R.string.last_information_update, date));
        if (profile.getLastSyncAttempt() != 0) {
            binding.tvLastUpdateAttempt.setText(getString(R.string.last_information_update_attempt, attempt));
        } else {
            binding.tvLastUpdateAttempt.setText(R.string.no_automatic_update_yet);
        }

        if (profile.getCourse() != null) binding.tvStdCourse.setText(profile.getCourse());
    }

    private void onReceiveSemesters(List<Semester> semesters) {
        if (semesters.size() == 0) {
            Timber.d("F R E S H M A N");
            binding.tvStdSemester.setText(R.string.student_freshman);
        } else {
            int currently = 0;
            for (Semester semester : semesters) {
                if (semester.getName().trim().length() == 5)
                    currently++;
            }
            binding.tvStdSemester.setText(getString(R.string.student_semester, currently));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (preferences.getBoolean("show_score", false)) {
            binding.tvStdScore.setVisibility(View.VISIBLE);
        } else {
            binding.tvStdScore.setVisibility(View.INVISIBLE);
        }

        if (preferences.getBoolean("show_current_semester", true)) {
            binding.tvStdSemester.setVisibility(View.VISIBLE);
            binding.tvStdSemesterHide.setVisibility(View.GONE);
        } else {
            binding.tvStdSemesterHide.setVisibility(View.VISIBLE);
            binding.tvStdSemester.setVisibility(View.GONE);
        }
    }

    private void goToUpdateControl() {
        ControlRoomActivity.startActivity(getContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_PROFILE_PICTURE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                try {
                    if (selectedImage != null) {
                        InputStream imageStream = requireActivity().getContentResolver().openInputStream(selectedImage);
                        Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);
                        binding.ivImgProfile.setImageBitmap(imageBitmap);
                        AnimUtils.fadeOut(requireContext(), binding.ivImgPlaceholder);
                        AnimUtils.fadeIn(requireContext(), binding.ivImgProfile);
                        controller.onProfileImageChanged(imageBitmap);
                        profileViewModel.saveProfileImageBitmap(imageBitmap);
                        if (!sharedPreferences.getBoolean("first_profile_image_set", false)) {
                            Toast.makeText(requireContext(), R.string.profile_image_unset, Toast.LENGTH_SHORT).show();
                            sharedPreferences.edit().putBoolean("first_profile_image_set", true).apply();
                        }
                    } else {
                        Timber.d("Selected image is null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), R.string.image_could_not_be_loaded, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @MainThread
    public void discoverProfilePicture() {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            TapTargetView.showFor(requireActivity(),
                    TapTarget.forView(binding.ivImgPlaceholder, getString(R.string.fd_profile_image), getString(R.string.fd_profile_image_desc))
                            .outerCircleColor(R.color.colorPrimary)
                            .targetCircleColor(R.color.white)
                            .titleTextColor(R.color.white)
                            .descriptionTextColor(R.color.white)
                            .dimColor(R.color.white)
                            .outerCircleAlpha(0.96f)
                            .descriptionTextSize(12)
                            .titleTextSize(20)
                            .textTypeface(Typeface.SANS_SERIF)
                            .drawShadow(true)
                            .cancelable(true)
                            .tintTarget(true)
                            .transparentTarget(true)
                            .targetRadius(70),
                    new TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            selectPicture();
                        }
                    }
            );
            sharedPreferences.edit().putBoolean("feature_discovered_profile_image", true).apply();
        }
    }
}
