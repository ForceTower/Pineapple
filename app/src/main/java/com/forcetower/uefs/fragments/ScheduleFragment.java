package com.forcetower.uefs.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;

import com.forcetower.uefs.R;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.adapters.DayScheduleAdapter;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.model.UClass;
import com.forcetower.uefs.model.UClassDay;

import java.util.HashMap;
import java.util.List;

public class ScheduleFragment extends Fragment {
    private Context context;
    private View rootView;
    private RecyclerView[] recyclerViews = new RecyclerView[7];
    private RelativeLayout[] relativeLayouts = new RelativeLayout[7];

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    public ScheduleFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        context = getActivity();

        configureRecyclerViews();
        configureRelativeLayouts();

        fillWithSchedule();

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
        HashMap<String, List<UClassDay>> classPerDay = ((UEFSApplication)getActivity().getApplication()).getSchedule();

        for (int i = 0; i < 7; i++) {
            String dayOfWeek = Utils.getDayOfWeek(i + 1);
            List<UClassDay> day = classPerDay.get(dayOfWeek);

            if (day.isEmpty()) {
                relativeLayouts[i].setVisibility(View.GONE);
            } else {
                relativeLayouts[i].setElevation(2);
                relativeLayouts[i].setBackgroundResource(android.R.color.white);
                recyclerViews[i].setLayoutManager(new LinearLayoutManager(context));
                recyclerViews[i].setAdapter(new DayScheduleAdapter(context, day, dayOfWeek));
                recyclerViews[i].addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                recyclerViews[i].setNestedScrollingEnabled(false);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
