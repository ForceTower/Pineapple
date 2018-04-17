package com.forcetower.uefs.view.connected.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.GameConnectionStatus;
import com.forcetower.uefs.R;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.GamesAccountController;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

/**
 * Created by João Paulo on 13/04/2018.
 */
public class TheAdventureFragment extends Fragment implements Injectable, EasyPermissions.PermissionCallbacks {
    public static final int REQUEST_PERMISSION_FINE_LOCATION = 6000;
    public static final int REQUEST_CHECK_SETTINGS = 6001;
    @BindView(R.id.tv_adventure_description)
    TextView tvAdventureDescription;
    @BindView(R.id.btn_join_adventure)
    Button btnJoin;
    @BindView(R.id.btn_logout_adventure)
    Button btnExit;
    @BindView(R.id.iv_unes_confirm_location)
    CircleImageView ivConfirmLocation;
    @BindView(R.id.tv_location_explained)
    TextView tvLocationExplained;

    private ActivityController actController;
    private GamesAccountController gameController;
    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences preferences;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actController = (ActivityController) context;
        gameController = (GamesAccountController) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_the_adventure, container, false);
        ButterKnife.bind(this, view);

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        actController.changeTitle(R.string.unes_the_adventure);
        actController.getTabLayout().setVisibility(View.GONE);
        setupInterface();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gameController.getPlayGamesInstance().getPlayGameStatus().observe(this, this::onPlayGameConnectionStatusChange);
    }

    private void onPlayGameConnectionStatusChange(GameConnectionStatus status) {
        if (status == GameConnectionStatus.CONNECTED) {
            showConnectedActions();
        } else if (status == GameConnectionStatus.DISCONNECTED) {
            showDisconnectedActions();
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
        btnJoin.setText(R.string.pref_unes_the_adventure_achievements);
        btnExit.setVisibility(View.VISIBLE);
        //AnimUtils.fadeIn(requireContext(), btnExit);
        tvAdventureDescription.setVisibility(View.GONE);

        if (!preferences.getBoolean("user_learned_confirm_location", false))
            tvLocationExplained.setVisibility(View.VISIBLE);

        Animation pulse = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse);
        ivConfirmLocation.startAnimation(pulse);
    }

    private void showDisconnectedActions() {
        btnJoin.setText(R.string.unes_the_adventure_join);
        //AnimUtils.fadeIn(requireContext(), btnJoin);
        btnExit.setVisibility(View.GONE);
        tvLocationExplained.setVisibility(View.GONE);
        //AnimUtils.fadeOutGone(requireContext(), btnExit);
        tvAdventureDescription.setVisibility(View.VISIBLE);
        ivConfirmLocation.clearAnimation();
    }

    @OnClick(value = R.id.btn_join_adventure)
    public void joinAdventure() {
        Timber.d("Clicked Join");
        if (gameController.getPlayGamesInstance().isSignedIn()) {
            gameController.openPlayGamesAchievements();
        } else {
            gameController.signIn();
        }
    }

    @OnClick(value = R.id.btn_logout_adventure)
    public void disconnectAdventure() {
        Timber.d("Clicked disconnect");
        gameController.getPlayGamesInstance().disconnect();
        showDisconnectedActions();
    }

    @OnClick(value = R.id.iv_unes_confirm_location)
    @AfterPermissionGranted(REQUEST_PERMISSION_FINE_LOCATION)
    public void confirmLocation() {
        Timber.d("Clicked to confirm location");
        preferences.edit().putBoolean("user_learned_confirm_location", true).apply();
        AnimUtils.fadeOutGone(requireContext(), tvLocationExplained);

        if (!gameController.getPlayGamesInstance().isSignedIn()) {
            Timber.d("Not connected to the adventure");
            return;
        }
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            checkLocation();
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.access_fine_location_request),
                    REQUEST_PERMISSION_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }


    private void checkLocation() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(requireContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnCompleteListener(requireActivity(), complete -> {
            Timber.d("Can make location request");

            try {
                fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        Timber.d("Location: LAT: %.8f - LOG: %.8f", location.getLatitude(), location.getLongitude());
                        Timber.d("Location: Accuracy: %.4f", location.getAccuracy());
                        if (location.getAccuracy() > 100) {
                            Toast.makeText(requireContext(), R.string.location_is_not_accurate, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        onReceiveLocation(location);
                    } else {
                        Timber.d("Location is null");
                        Toast.makeText(requireContext(), R.string.cant_receive_location, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (SecurityException e) {
                Timber.e("What??? How did this happen?");
            }

        }).addOnFailureListener(requireActivity(), fail -> {
            if (fail instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) fail;
                    resolvable.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS);
                } catch (Exception ignored) {
                    Timber.d("Ignored exception");
                    ignored.printStackTrace();
                    Toast.makeText(requireContext(), R.string.cant_receive_location, Toast.LENGTH_SHORT).show();
                }
            } else {
                Timber.d("Unresolvable Exception");
                fail.printStackTrace();
                Toast.makeText(requireContext(), R.string.cant_receive_location, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onReceiveLocation(@NonNull Location location) {
        if (matchesLibrary(location)) {
            gameController.unlockAchievements(getString(R.string.achievement_dora_the_studious), gameController.getPlayGamesInstance());
        } else if (matchesZoologyMuseum(location)) {
            gameController.unlockAchievements(getString(R.string.achievement_dora_the_adventurer), gameController.getPlayGamesInstance());
        } else {
            Timber.d("Not in a valid location");
            Toast.makeText(requireContext(), R.string.adventure_not_in_a_valid_location, Toast.LENGTH_SHORT).show();
        }
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
