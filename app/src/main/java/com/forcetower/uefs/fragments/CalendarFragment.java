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
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.adapters.ui.CalendarAdapter;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresCalendarItem;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.exception.SagresInfoFetchException;
import com.forcetower.uefs.sagres_sdk.utility.SagresUtility;

import java.util.List;

/**
 * Created by João Paulo on 02/12/2017.
 */

public class CalendarFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView recyclerView;
    private CalendarAdapter calendarAdapter;
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout relativeLayout;
    private boolean canRefresh = true;
    private Handler handler;
    private Context context;
    private Runnable refreshDelay = new Runnable() {
        @Override
        public void run() {
            canRefresh = true;
        }
    };

    public CalendarFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        recyclerView = rootView.findViewById(R.id.rv_calendar);
        relativeLayout = rootView.findViewById(R.id.rl_root);
        refreshLayout = rootView.findViewById(R.id.calendar_swipe_refresh);
        refreshLayout.setOnRefreshListener(this);

        if (Utils.isLollipop()) {
            rootView.setNestedScrollingEnabled(false);
            relativeLayout.setNestedScrollingEnabled(false);
        }
        
        fillWithCalendar();

        return rootView;
    }

    private void fillWithCalendar() {
        if (SagresProfile.getCurrentProfile() == null) {
            return;
        }
        if (Utils.isLollipop()) relativeLayout.setElevation(5);

        List<SagresCalendarItem> calendar = SagresProfile.getCurrentProfile().getCalendar();
        calendarAdapter = new CalendarAdapter(context, calendar);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(calendarAdapter);
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
        List<SagresCalendarItem> calendar = SagresProfile.getCurrentProfile().getCalendar();
        calendarAdapter.setCalendar(calendar);
    }

    //TODO This part can be done into the Major Activity
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
                updateView(profile);
            }

            @Override
            public void onInvalidLogin() {
                showTextOnToast(R.string.incorrect_credentials);
                updateView(null);
            }

            @Override
            public void onDeveloperError() {
                showTextOnToast(R.string.this_is_and_error);
                updateView(null);
            }

            @Override
            public void onFailure(SagresInfoFetchException e) {
                showTextOnToast(R.string.this_is_and_error);
                updateView(null);
            }

            @Override
            public void onFailedConnect() {
                showTextOnToast(R.string.network_error);
                updateView(null);
            }

            @Override
            public void onHalfCompleted(int completedSteps) {
                if (completedSteps == 1) {
                    showTextOnToast(R.string.unable_to_fetch_grades);
                }
            }
        });
    }

    private void updateView(final SagresProfile profile) {
        assert getActivity() != null;
        if (getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                if (profile == null) {
                    return;
                }
                calendarAdapter.setCalendar(profile.getCalendar());
            }
        });
    }

    private void showTextOnToast(@StringRes final int resId) {
        assert getActivity() != null;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
