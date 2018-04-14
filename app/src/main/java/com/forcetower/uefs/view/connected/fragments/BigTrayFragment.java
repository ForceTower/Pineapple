package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.ru.RUData;
import com.forcetower.uefs.ru.RUFirebase;
import com.forcetower.uefs.ru.RUtils;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.DateUtils;
import com.forcetower.uefs.view.connected.ActivityController;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BigTrayFragment extends Fragment implements Injectable {
    @BindView(R.id.tv_ru_state)
    TextView tvRuState;
    @BindView(R.id.tv_ru_meal)
    TextView tvRuMeal;
    @BindView(R.id.tv_ru_amount)
    TextView tvRuAmount;
    @BindView(R.id.tv_ru_meal_time)
    TextView tvRuMealTime;
    @BindView(R.id.tv_ru_meal_price)
    TextView tvRuPrice;
    @BindView(R.id.tv_ru_last_update)
    TextView tvRuLastUpdate;
    @BindView(R.id.sv_ru_loaded)
    ScrollView svRUContent;
    @BindView(R.id.ll_btns)
    LinearLayout llBtns;
    @BindView(R.id.tv_ru_loading)
    TextView tvLoading;

    @Inject
    RUFirebase ruFirebase;

    private ActivityController controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_big_tray, container, false);
        ButterKnife.bind(this, view);

        controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_big_tray);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DatabaseReference reference = ruFirebase.getFirebaseDatabase().getReference("bandejao");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                RUData data = snapshot.getValue(RUData.class);
                if (data != null) updateInterface(data);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void updateInterface(RUData data) {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED) && getActivity() != null)
            bindData(data);
    }

    @UiThread
    private void bindData(RUData data) {
        AnimUtils.fadeOutGone(requireContext(), tvLoading);
        AnimUtils.fadeIn(requireContext(), llBtns);
        AnimUtils.fadeIn(requireContext(), svRUContent);

        boolean open = data.isAberto();
        Integer amount = 0;
        try { amount = Integer.parseInt(data.getCotas().get(0)); } catch (Exception ignored) {}
        String time = data.getTime();
        Calendar calendar = RUtils.convertTime(time);
        int mealType = RUtils.getNextMealType(calendar);

        if (RUtils.isOpen(open, amount, mealType)) {
            tvRuState.setText(R.string.the_big_tray_is_open);
            tvRuMeal.setText(getString(R.string.ru_meal_name_partial, RUtils.getNextMeal(requireActivity(), mealType)));
            tvRuAmount.setVisibility(View.VISIBLE);
            tvRuAmount.setText(getString(R.string.ru_amount_format, amount));
            tvRuMealTime.setText(RUtils.getNextMealTime(calendar));
            tvRuPrice.setVisibility(View.VISIBLE);
            tvRuPrice.setText(RUtils.getPrice(mealType, amount));
        } else {
            tvRuState.setText(R.string.the_big_tray_is_closed);
            tvRuMeal.setText(RUtils.getNextMeal(requireActivity(), mealType));
            tvRuAmount.setVisibility(View.GONE);
            tvRuMealTime.setText(RUtils.getNextMealTime(calendar));
            tvRuPrice.setVisibility(View.GONE);
        }

        tvRuLastUpdate.setText(getString(R.string.ru_last_update, DateUtils.formatDateTime(calendar.getTimeInMillis())));
    }
}
