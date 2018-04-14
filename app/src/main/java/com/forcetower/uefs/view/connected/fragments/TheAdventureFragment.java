package com.forcetower.uefs.view.connected.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.forcetower.uefs.GameConnectionStatus;
import com.forcetower.uefs.R;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.GamesAccountController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

/**
 * Created by João Paulo on 13/04/2018.
 */
public class TheAdventureFragment extends Fragment implements Injectable {
    @BindView(R.id.tv_adventure_description)
    TextView tvAdventureDescription;
    @BindView(R.id.btn_join_adventure)
    Button btnJoin;
    @BindView(R.id.btn_logout_adventure)
    Button btnExit;
    @BindView(R.id.iv_unes_confirm_location)
    CircleImageView ivConfirmLocation;

    private ActivityController actController;
    private GamesAccountController gameController;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actController = (ActivityController) context;
        gameController = (GamesAccountController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_the_adventure, container, false);
        ButterKnife.bind(this, view);

        actController.changeTitle(R.string.unes_the_adventure);
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
        AnimUtils.fadeIn(requireContext(), btnExit);
        tvAdventureDescription.setVisibility(View.GONE);
    }

    private void showDisconnectedActions() {
        btnJoin.setText(R.string.unes_the_adventure_join);
        AnimUtils.fadeIn(requireContext(), btnJoin);
        AnimUtils.fadeOutGone(requireContext(), btnExit);
        tvAdventureDescription.setVisibility(View.VISIBLE);
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
    public void confirmLocation() {
        Timber.d("Clicked to confirm location");
    }
}
