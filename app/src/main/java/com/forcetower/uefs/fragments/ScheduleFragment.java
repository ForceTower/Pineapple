package com.forcetower.uefs.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.ClassDetailsActivity;
import com.forcetower.uefs.adapters.ui.DayScheduleAdapter;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDay;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

import java.util.HashMap;
import java.util.List;

public class ScheduleFragment extends Fragment {
    private Context context;
    private View rootView;
    private RecyclerView[] recyclerViews = new RecyclerView[7];
    private RelativeLayout[] relativeLayouts = new RelativeLayout[7];
    private DayScheduleAdapter.OnClassClickListener classClickListener = new DayScheduleAdapter.OnClassClickListener() {
        @Override
        public void onClassClicked(View view, int position, SagresClassDay classDay) {
            ClassDetailsActivity.startActivity(context, "TEC412", "20172", "T01");
        }
    };

    public ScheduleFragment() {
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_refresh);
        if (item != null) item.setVisible(false);
    }

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        context = getActivity();


        if (Utils.isLollipop()) {
            rootView.setNestedScrollingEnabled(false);
            rootView.findViewById(R.id.classes_ll_view).setNestedScrollingEnabled(false);
        }
        configureRecyclerViews();
        configureRelativeLayouts();

        fillWithSchedule();
        setHasOptionsMenu(true);

        return rootView;
    }

    private void configureRecyclerViews() {
        recyclerViews[0] = rootView.findViewById(R.id.rv_schedule_mon);
        recyclerViews[1] = rootView.findViewById(R.id.rv_schedule_tue);
        recyclerViews[2] = rootView.findViewById(R.id.rv_schedule_wed);
        recyclerViews[3] = rootView.findViewById(R.id.rv_schedule_thu);
        recyclerViews[4] = rootView.findViewById(R.id.rv_schedule_fri);
        recyclerViews[5] = rootView.findViewById(R.id.rv_schedule_sat);
        recyclerViews[6] = rootView.findViewById(R.id.rv_schedule_sun);
    }

    private void configureRelativeLayouts() {
        relativeLayouts[0] = rootView.findViewById(R.id.rl_mon);
        relativeLayouts[1] = rootView.findViewById(R.id.rl_tue);
        relativeLayouts[2] = rootView.findViewById(R.id.rl_wed);
        relativeLayouts[3] = rootView.findViewById(R.id.rl_thu);
        relativeLayouts[4] = rootView.findViewById(R.id.rl_fri);
        relativeLayouts[5] = rootView.findViewById(R.id.rl_sat);
        relativeLayouts[6] = rootView.findViewById(R.id.rl_sun);
    }

    private void fillWithSchedule() {
        if (getActivity() != null) {
            HashMap<String, List<SagresClassDay>> classPerDay = SagresProfile.getCurrentProfile().getClasses();

            for (int i = 0; i < 7; i++) {
                String dayOfWeek = Utils.getDayOfWeek(i + 1);
                List<SagresClassDay> day = classPerDay.get(dayOfWeek);

                if (day.isEmpty()) {
                    relativeLayouts[i].setVisibility(View.GONE);
                } else {
                    if (Utils.isLollipop()) relativeLayouts[i].setElevation(2);
                    relativeLayouts[i].setBackgroundResource(android.R.color.white);
                    recyclerViews[i].setLayoutManager(new LinearLayoutManager(context));

                    DayScheduleAdapter dayScheduleAdapter = new DayScheduleAdapter(context, day, dayOfWeek);
                    dayScheduleAdapter.setClickListener(classClickListener);

                    recyclerViews[i].setAdapter(dayScheduleAdapter);
                    recyclerViews[i].addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                    recyclerViews[i].setNestedScrollingEnabled(false);
                }
            }
        } else {
            Log.e(Constants.APP_TAG, "Activity attached to ScheduleFragment is null. Bug generated by API16");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
