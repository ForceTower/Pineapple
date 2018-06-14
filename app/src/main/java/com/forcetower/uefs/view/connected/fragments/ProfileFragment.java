package com.forcetower.uefs.view.connected.fragments;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.BuildConfig;
import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db.entity.Semester;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.DateUtils;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.control_room.ControlRoomActivity;
import com.forcetower.uefs.vm.base.ProfileViewModel;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static android.os.Looper.getMainLooper;
import static com.forcetower.uefs.view.connected.LoggedActivity.BACKGROUND_IMAGE;

/**
 * Created by João Paulo on 08/03/2018.
 */

public class ProfileFragment extends Fragment implements Injectable {
    public static final int REQUEST_SELECT_PROFILE_PICTURE = 5000;
    @BindView(R.id.tv_std_name)
    TextView tvStdName;
    @BindView(R.id.tv_std_semester)
    TextView tvStdSemester;
    @BindView(R.id.tv_std_semester_hide)
    TextView tvStdSemesterHidden;
    @BindView(R.id.tv_std_score)
    TextView tvStdScore;
    @BindView(R.id.tv_last_update)
    TextView tvLastUpdate;
    @BindView(R.id.tv_last_update_attempt)
    TextView tvLastUpdateAttempt;
    @BindView(R.id.iv_background)
    ImageView ivBackground;
    @BindView(R.id.vw_bg_alpha)
    View vwBgAlpha;

    @BindView(R.id.cv_update_control)
    CardView cvUpdateControl;
    @BindView(R.id.iv_img_profile)
    CircleImageView ivProfileImage;
    @BindView(R.id.iv_img_placeholder)
    CircleImageView ivProfilePlaceholder;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ProfileViewModel profileViewModel;
    private SharedPreferences sharedPreferences;

    private ActivityController controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d(String.valueOf(getParentFragment()));
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_profile);

        cvUpdateControl.setOnClickListener(v -> goToUpdateControl());

        if (BuildConfig.DEBUG) enablePrivateContent();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (sharedPreferences.getBoolean("show_score", false)) {
            tvStdScore.setVisibility(View.VISIBLE);
        }

        if (!sharedPreferences.getBoolean("feature_discovered_profile_image", false)) {
            new Handler(getMainLooper()).postDelayed(this::discoverProfilePicture, 500);
        }

        String backgroundImage = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString(BACKGROUND_IMAGE, Constants.BACKGROUND_IMAGE_DEFAULT);
        Picasso.with(requireContext()).load(backgroundImage).into(ivBackground, new Callback() {
            @Override
            public void onSuccess() { vwBgAlpha.setVisibility(View.VISIBLE); }
            @Override
            public void onError() { vwBgAlpha.setVisibility(View.INVISIBLE); }
        });

        return view;
    }

    @OnClick(value = {R.id.iv_img_profile, R.id.iv_img_placeholder})
    public void selectPicture() {
        Timber.d("Fired on click listener");
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_SELECT_PROFILE_PICTURE);
    }

    @OnLongClick(value = R.id.iv_img_profile)
    public boolean onProfileImageLongClick() {
        AnimUtils.fadeOut(requireContext(), ivProfileImage);
        AnimUtils.fadeIn(requireContext(), ivProfilePlaceholder);
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
    }

    private void onReceiveProfileImage(Bitmap bitmap) {
        controller.onProfileImageChanged(bitmap);

        if (bitmap == null) {
            Timber.d("No image set so far");
            AnimUtils.fadeIn(requireContext(), ivProfilePlaceholder);
            AnimUtils.fadeOut(requireContext(), ivProfileImage);
            return;
        }

        ivProfileImage.setImageBitmap(bitmap);
        AnimUtils.fadeIn(requireContext(), ivProfileImage);
        AnimUtils.fadeOut(requireContext(), ivProfilePlaceholder);
    }

    private void onReceiveAccess(Access access) {
        if (access == null) return;

        if (access.getUsername().equalsIgnoreCase("jpssena")) {
            enablePrivateContent();
        }
    }

    private void enablePrivateContent() {
        cvUpdateControl.setVisibility(View.VISIBLE);
    }

    private void onReceiveProfile(Profile profile) {
        if (profile == null) return;

        tvStdName.setText(profile.getName());

        if (profile.getScore() >= 0) {
            tvStdScore.setText(getString(R.string.student_score, profile.getScore()));
        } else {
            tvStdScore.setText(R.string.no_score_message);
        }

        Timber.d("Last Sync: %s", profile.getLastSync());
        String date = DateUtils.convertTime(profile.getLastSync());
        String attempt = DateUtils.convertTime(profile.getLastSyncAttempt());
        tvLastUpdate.setText(getString(R.string.last_information_update, date));
        if (profile.getLastSyncAttempt() != 0) {
            tvLastUpdateAttempt.setText(getString(R.string.last_information_update_attempt, attempt));
        } else {
            tvLastUpdateAttempt.setText(R.string.no_automatic_update_yet);
        }
    }

    private void onReceiveSemesters(List<Semester> semesters) {
        if (semesters.size() == 0) {
            Timber.d("F R E S H M A N");
            tvStdSemester.setText(R.string.student_freshman);
        } else {
            int currently = 0;
            for (Semester semester : semesters) {
                if (semester.getName().trim().length() == 5)
                    currently++;
            }
            tvStdSemester.setText(getString(R.string.student_semester, currently));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (preferences.getBoolean("show_score", false)) {
            tvStdScore.setVisibility(View.VISIBLE);
        } else {
            tvStdScore.setVisibility(View.INVISIBLE);
        }

        if (preferences.getBoolean("show_current_semester", true)) {
            tvStdSemester.setVisibility(View.VISIBLE);
            tvStdSemesterHidden.setVisibility(View.GONE);
        } else {
            tvStdSemesterHidden.setVisibility(View.VISIBLE);
            tvStdSemester.setVisibility(View.GONE);
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
                        ivProfileImage.setImageBitmap(imageBitmap);
                        AnimUtils.fadeOut(requireContext(), ivProfilePlaceholder);
                        AnimUtils.fadeIn(requireContext(), ivProfileImage);
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
                    TapTarget.forView(ivProfilePlaceholder, getString(R.string.fd_profile_image), getString(R.string.fd_profile_image_desc))
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
