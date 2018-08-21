package com.forcetower.uefs.view.connected.fragments;

import android.Manifest;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.forcetower.uefs.BuildConfig;
import com.forcetower.uefs.GameConnectionStatus;
import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentTheAdventureBinding;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.GamesAccountController;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

/**
 * Created by João Paulo on 13/04/2018.
 */
public class TheAdventureFragment extends Fragment implements Injectable, EasyPermissions.PermissionCallbacks {
    public static final int REQUEST_PERMISSION_FINE_LOCATION = 6000;
    public static final int REQUEST_CHECK_SETTINGS = 6001;
    public static final String SHOW_ACCURACY_MESSAGE_KEY = "showed_message";
    public static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting_location_updates";

    private ActivityController actController;
    private GamesAccountController gameController;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest mLocationRequest;

    private FragmentTheAdventureBinding binding;

    private boolean showedMessage = false;
    private boolean requestingLocationUpdates = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actController = (ActivityController) context;
        gameController = (GamesAccountController) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            showedMessage = savedInstanceState.getBoolean(SHOW_ACCURACY_MESSAGE_KEY, false);
            requestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY, false);
        }

        locationSettings();
    }

    private void locationSettings() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    if (location.getAccuracy() > 100) {
                        if (!showedMessage) {
                            Toast.makeText(requireContext(), R.string.location_is_not_accurate, Toast.LENGTH_SHORT).show();
                            showedMessage = true;
                        }
                        return;
                    }
                    onReceiveLocation(location);
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_the_adventure, container, false);
        binding.btnLogoutAdventure.setOnClickListener(v -> disconnectAdventure());
        binding.btnJoinAdventure.setOnClickListener(v -> joinAdventure());
        binding.ivUnesConfirmLocation.setOnClickListener(v -> confirmLocation());
        actController.changeTitle(R.string.unes_the_adventure);
        actController.getTabLayout().setVisibility(View.GONE);
        setupInterface();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gameController.getPlayGamesInstance().getPlayGameStatus().observe(this, this::onPlayGameConnectionStatusChange);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestingLocationUpdates)
            startLocationUpdates();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates);
        outState.putBoolean(SHOW_ACCURACY_MESSAGE_KEY, showedMessage);
    }

    private void onPlayGameConnectionStatusChange(GameConnectionStatus status) {
        if (status == GameConnectionStatus.CONNECTED) {
            showConnectedActions();
        } else if (status == GameConnectionStatus.DISCONNECTED) {
            showDisconnectedActions();
            stopLocationUpdates();
        }
    }

    private void setupInterface() {
        if (gameController.getPlayGamesInstance().isSignedIn()) {
            showConnectedActions();
        } else {
            showDisconnectedActions();
        }
    }

    private void showConnectedActions() {
        binding.btnJoinAdventure.setText(R.string.pref_unes_the_adventure_achievements);
        binding.btnLogoutAdventure.setVisibility(View.VISIBLE);
        binding.tvAdventureDescription.setVisibility(View.GONE);
        binding.tvLocationExplained.setVisibility(View.VISIBLE);
    }

    private void showDisconnectedActions() {
        binding.btnJoinAdventure.setText(R.string.unes_the_adventure_join);
        binding.btnLogoutAdventure.setVisibility(View.GONE);
        binding.tvLocationExplained.setVisibility(View.GONE);
        binding.tvAdventureDescription.setVisibility(View.VISIBLE);
        binding.ivUnesConfirmLocation.clearAnimation();
    }

    public void joinAdventure() {
        Timber.d("Clicked Join");
        if (gameController.getPlayGamesInstance().isSignedIn()) {
            gameController.openPlayGamesAchievements();
        } else {
            gameController.signIn();
        }
    }

    public void disconnectAdventure() {
        Timber.d("Clicked disconnect");
        gameController.getPlayGamesInstance().disconnect();
        showDisconnectedActions();
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_FINE_LOCATION)
    public void confirmLocation() {
        Timber.d("Clicked to confirm location");
        //preferences.edit().putBoolean("user_learned_confirm_location", true).apply();
        //AnimUtils.fadeOutGone(requireContext(), tvLocationExplained);

        if (!gameController.getPlayGamesInstance().isSignedIn()) {
            Timber.d("Not connected to the adventure");
            return;
        }
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (!requestingLocationUpdates) {
                requestingLocationUpdates = true;
                checkLocation();
            } else {
                stopLocationUpdates();
                requestingLocationUpdates = false;
            }
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.access_fine_location_request),
                    REQUEST_PERMISSION_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }


    private void checkLocation() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(7000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(requireContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnCompleteListener(requireActivity(), complete -> {
            Timber.d("Can make location request");

            try {
                startLocationUpdates();
            } catch (SecurityException e) {
                Timber.e("What??? How did this happen?");
            }

        }).addOnFailureListener(requireActivity(), fail -> {
            if (fail instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) fail;
                    resolvable.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS);
                } catch (Exception e) {
                    Timber.d("Ignored exception");
                    e.printStackTrace();
                    Toast.makeText(requireContext(), R.string.cant_receive_location, Toast.LENGTH_SHORT).show();
                }
            } else {
                Timber.d("Unresolvable Exception");
                fail.printStackTrace();
                Toast.makeText(requireContext(), R.string.cant_receive_location, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startLocationUpdates() {
        try {
            if (mLocationRequest == null) checkLocation();

            fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
            Animation pulse = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse);
            binding.ivUnesConfirmLocation.startAnimation(pulse);
        } catch (SecurityException e) {
            Timber.d("Method could not be called");
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        binding.ivUnesConfirmLocation.clearAnimation();
    }

    private void onReceiveLocation(@NonNull Location location) {
        if (matchesLibrary(location)) {
            gameController.unlockAchievements(getString(R.string.achievement_dora_the_studious), gameController.getPlayGamesInstance());
        } else if (matchesZoologyMuseum(location)) {
            gameController.unlockAchievements(getString(R.string.achievement_dora_the_adventurer), gameController.getPlayGamesInstance());
        } else if (matchesBigTray(location)) {
            gameController.unlockAchievements(getString(R.string.achievement_big_tray_location), gameController.getPlayGamesInstance());
        } else {
            Timber.d("Not in a valid location");
            if (BuildConfig.DEBUG) Toast.makeText(requireContext(), R.string.adventure_not_in_a_valid_location, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean matchesBigTray(@NonNull Location location) {
        Location bigTray = new Location("");
        bigTray.setLatitude(-12.201868);
        bigTray.setLongitude(-38.96974);

        float distance = location.distanceTo(bigTray);
        Timber.d("Distance to big tray: %f", distance);

        if (distance - location.getAccuracy() <= 20) {
            Timber.d("You unlocked by measure");
            return true;
        }
        
        return false;
    }

    private boolean matchesZoologyMuseum(@NonNull Location location) {
        Location museum = new Location("");
        museum.setLatitude(-12.198888);
        museum.setLongitude(-38.967986);

        float distance = location.distanceTo(museum);
        Timber.d("Distance to Serpents: %f", distance);

        if (distance <= 15) {
            Timber.d("You unlocked by measure");
            return true;
        }

        if (location.getLatitude() < -12.199053 || location.getLatitude() > -12.198730)
            return false;
        if (location.getLongitude() > -38.967865 || location.getLongitude() < -38.968281)
            return false;

        return true;
    }

    private boolean matchesLibrary(@NonNull Location location) {
        Location library = new Location("");
        library.setLatitude(-12.202193);
        library.setLongitude(-38.972065);

        float distance = location.distanceTo(library);
        Timber.d("Distance to library: %f", distance);
        if (distance <= 15) {
            Timber.d("You unlocked by measure");
            return true;
        }

        if (location.getLatitude() < -12.202489 || location.getLatitude() > -12.201988)
            return false;
        if (location.getLongitude() > -38.971906 || location.getLongitude() < -38.972303)
            return false;

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) { }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) { }
}
