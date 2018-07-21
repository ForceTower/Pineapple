package com.forcetower.uefs.view.control_room.fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentSendNotificationBinding;
import com.forcetower.uefs.db_service.helper.ImGurDataObject;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.service.NotificationViewModel;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;

import javax.inject.Inject;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.forcetower.uefs.util.WordUtils.validString;

public class SendNotificationFragment extends Fragment implements Injectable {
    private static final int REQUEST_SELECT_NOTIFICATION_PICTURE = 8100;
    @Inject
    UEFSViewModelFactory viewModelFactory;

    private boolean canChange = true;
    private FragmentSendNotificationBinding binding;
    private NotificationViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_notification, container, false);
        binding.notificationWithImage.setOnCheckedChangeListener((compoundButton, value) -> {
            if (value) binding.imageContainer.setVisibility(View.VISIBLE);
            else binding.imageContainer.setVisibility(View.GONE);
        });

        binding.notificationImage.setOnClickListener(v -> onImageClick());
        binding.sendNotification.setOnClickListener(v -> onSendClick());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NotificationViewModel.class);
        viewModel.getBlurImage().observe(this, this::onBlurImageUpdate);
        viewModel.getUploadImGur().observe(this, this::onEventImageUploaded);
        viewModel.getSendNotification().observe(this, this::onNotificationSend);
    }

    private void onNotificationSend(Resource<ActionResult<String>> result) {
        switch (result.status) {
            case LOADING:
                Timber.d("Loading");
                break;
            case ERROR:
                Toast.makeText(requireContext(), result.actionError != null ? result.actionError.getMessage() : result.message, Toast.LENGTH_SHORT).show();
                break;
            case SUCCESS:
                Toast.makeText(requireContext(), R.string.notification_sent, Toast.LENGTH_SHORT).show();
                requireActivity().finish();
                break;
        }
    }

    private void onSendClick() {
        String title = binding.notificationTitle.getText().toString();
        String message = binding.notificationMessage.getText().toString();
        boolean withImage = binding.notificationWithImage.isChecked();

        viewModel.sendNotification(title, message, withImage);
    }

    private void onImageClick() {
        if (!canChange) {
            Toast.makeText(requireContext(), R.string.wait_until_image_is_processed, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_SELECT_NOTIFICATION_PICTURE);
    }

    private void onBlurImageUpdate(Bitmap bitmap) {
        if (bitmap != null && !canChange) {
            binding.notificationImage.setImageBitmap(bitmap);
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
                    viewModel.setImageUrl(resource.data.getLink());
                    Timber.d("Image delete Hash: " + resource.data.getDeleteHash());
                    viewModel.setDeleteHash(resource.data.getDeleteHash());
                    refreshInterface(resource.data.getLink());
                } else {
                    Timber.d("Resource Data: " + resource.data);
                    Toast.makeText(requireContext(), R.string.upload_failed, Toast.LENGTH_SHORT).show();
                }
                break;
        }
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

    private void refreshInterface(String url) {
        if (validString(url)) {
            Picasso.with(requireContext()).load(url).into(binding.notificationImage);
        } else {
            Timber.d("Invalid image for now. Image: " + url);
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

        if (requestCode == REQUEST_SELECT_NOTIFICATION_PICTURE) {
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
