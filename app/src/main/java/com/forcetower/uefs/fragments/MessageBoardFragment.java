package com.forcetower.uefs.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.MessageActivity;
import com.forcetower.uefs.adapters.ui.MessageBoardAdapter;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.exception.SagresInfoFetchException;
import com.forcetower.uefs.sagres_sdk.managers.SagresProfileManager;
import com.forcetower.uefs.sagres_sdk.utility.SagresUtility;

import java.util.List;

/**
 * Created by João Paulo on 18/11/2017.
 */

public class MessageBoardFragment extends Fragment {
    private Context context;
    private RecyclerView recyclerView;
    private RelativeLayout relativeLayout;
    private MessageBoardAdapter messageAdapter;
    private SwipeRefreshLayout refreshLayout;
    private View rootView;
    private boolean canRefresh = true;
    private Handler handler;
    private MessageBoardAdapter.OnMessageClickListener onMessageClickListener = new MessageBoardAdapter.OnMessageClickListener() {
        @Override
        public void onMessageClicked(View view, int position, SagresMessage message) {
            MessageActivity.startActivity(context, message);
        }
    };
    private Runnable refreshDelay = new Runnable() {
        @Override
        public void run() {
            canRefresh = true;
        }
    };
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!canRefresh) {
                refreshLayout.setRefreshing(false);
                showTextOnToast(R.string.wait_before_updating_again);
                return;
            }

            canRefresh = false;
            handler.postDelayed(refreshDelay, 20000);

            refreshLayout.setRefreshing(true);
            SagresProfile.asyncFetchProfileInformationWithCallback(new SagresUtility.AsyncFetchProfileInformationCallback() {
                @Override
                public void onSuccess(SagresProfile profile) {
                    updateView(profile, false);
                }

                @Override
                public void onInvalidLogin() {
                    showTextOnToast(R.string.incorrect_credentials);
                    updateView(null, false);
                }

                @Override
                public void onDeveloperError() {
                    showTextOnToast(R.string.this_is_and_error);
                    updateView(null, false);
                }

                @Override
                public void onFailure(SagresInfoFetchException e) {
                    showTextOnToast(R.string.this_is_and_error);
                    updateView(null, false);
                }

                @Override
                public void onFailedConnect() {
                    showTextOnToast(R.string.network_error);
                    updateView(null, false);
                }

                @Override
                public void onHalfCompleted(int completedSteps) {
                    if (completedSteps == 1) {
                        showTextOnToast(R.string.unable_to_fetch_grades);
                    }
                }
            });
        }
    };

    public MessageBoardFragment() {
    }

    public static MessageBoardFragment newInstance() {
        return new MessageBoardFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_refresh);
        if (item != null) item.setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message_board, container, false);

        relativeLayout = rootView.findViewById(R.id.rl_root);
        recyclerView = rootView.findViewById(R.id.rv_messages);
        refreshLayout = rootView.findViewById(R.id.message_swipe_refresh);

        refreshLayout.setOnRefreshListener(refreshListener);

        if (Utils.isLollipop()) {
            rootView.setNestedScrollingEnabled(false);
            relativeLayout.setNestedScrollingEnabled(false);
        }
        setHasOptionsMenu(true);
        fillWithMessages();

        return rootView;
    }

    private void fillWithMessages() {
        List<SagresMessage> messages = SagresProfile.getCurrentProfile().getMessages();

        messageAdapter = new MessageBoardAdapter(context, messages);
        messageAdapter.setOnMessageClickListener(onMessageClickListener);

        if (Utils.isLollipop()) relativeLayout.setElevation(2);
        relativeLayout.setBackgroundResource(android.R.color.white);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(messageAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(context.getMainLooper());
    }

    @Override
    public void onResume() {
        super.onResume();
        SagresProfileManager.getInstance().loadCurrentProfile();
        List<SagresMessage> messages = SagresProfile.getCurrentProfile().getMessages();
        messageAdapter.setMessageList(messages);
    }

    private void updateView(final SagresProfile profile, final boolean refreshing) {
        assert getActivity() != null;
        if (getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(refreshing);
                if (profile == null) {
                    return;
                }
                messageAdapter.setMessageList(profile.getMessages());
            }
        });
    }

    private void showTextOnToast(@StringRes final int resId) {
        assert getActivity() != null;
        if (!isVisible())
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
