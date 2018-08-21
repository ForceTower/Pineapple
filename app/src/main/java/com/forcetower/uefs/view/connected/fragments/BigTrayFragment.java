package com.forcetower.uefs.view.connected.fragments;

import androidx.lifecycle.Lifecycle;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentBigTrayBinding;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.ru.RUData;
import com.forcetower.uefs.ru.RUFirebase;
import com.forcetower.uefs.ru.RUtils;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.DateUtils;
import com.forcetower.uefs.util.NetworkUtils;
import com.forcetower.uefs.view.connected.ActivityController;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import javax.inject.Inject;

import timber.log.Timber;

public class BigTrayFragment extends Fragment implements Injectable {
    @Inject
    RUFirebase ruFirebase;

    private ActivityController controller;
    private DatabaseReference reference;
    private FragmentBigTrayBinding binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_big_tray, container, false);

        if (controller.getTabLayout() != null) controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_big_tray);
        reference = ruFirebase.getFirebaseDatabase().getReference("bandejao");

        binding.btnVisitBigTray.setOnClickListener(v -> NetworkUtils.openLink(requireContext(), "http://bit.ly/bandejaouefs"));
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        reference.addValueEventListener(valueListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        reference.removeEventListener(valueListener);
    }

    private void updateInterface(RUData data) {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED) && getActivity() != null)
            bindData(data);
    }

    private void updateErrorInterface() {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED) && getActivity() != null) {
            AnimUtils.fadeOutGone(requireContext(), binding.tvRuLoading);
            AnimUtils.fadeIn(requireContext(), binding.llBtns);
            AnimUtils.fadeIn(requireContext(), binding.tvRuError);
        }
    }

    @UiThread
    private void bindData(RUData data) {
        AnimUtils.fadeOutGone(requireContext(), binding.tvRuLoading);
        AnimUtils.fadeOutGone(requireContext(), binding.tvRuError);
        AnimUtils.fadeIn(requireContext(), binding.llBtns);
        AnimUtils.fadeIn(requireContext(), binding.svRuLoaded);

        boolean open = data.isAberto();
        Integer amount = 0;
        try { amount = Integer.parseInt(data.getCotas().get(0)); } catch (Exception ignored) {}
        String time = data.getTime();
        Calendar calendar = RUtils.convertTime(time);
        int mealType = RUtils.getNextMealType(calendar);

        if (RUtils.isOpen(open, amount, mealType)) {
            binding.tvRuState.setText(R.string.the_big_tray_is_open);
            binding.tvRuMeal.setText(getString(R.string.ru_meal_name_partial, RUtils.getNextMeal(requireContext(), mealType)));
            binding.tvRuAmount.setVisibility(View.VISIBLE);
            binding.tvRuAmount.setText(getString(R.string.ru_amount_format, amount));
            binding.tvRuMealTime.setText(RUtils.getNextMealTime(calendar));
            binding.tvRuMealPrice.setVisibility(View.VISIBLE);
            binding.tvRuApproxLabel.setVisibility(View.VISIBLE);
            binding.tvRuMealPrice.setText(RUtils.getPrice(mealType, amount));
        } else {
            binding.tvRuState.setText(R.string.the_big_tray_is_closed);
            binding.tvRuMeal.setText(RUtils.getNextMeal(requireContext(), mealType));
            binding.tvRuAmount.setVisibility(View.GONE);
            binding.tvRuMealTime.setText(RUtils.getNextMealTime(calendar));
            binding.tvRuMealPrice.setVisibility(View.GONE);
            binding.tvRuApproxLabel.setVisibility(View.GONE);
        }

        binding.tvRuLastUpdate.setText(getString(R.string.ru_last_update, DateUtils.formatDateTime(calendar.getTimeInMillis())));
    }

    private ValueEventListener valueListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            try {
                RUData data = snapshot.getValue(RUData.class);
                if (data != null) updateInterface(data);
            } catch (Exception e) {
                Timber.e("This RU foreplay is just funny");
                Crashlytics.logException(e);
                Crashlytics.log("RU Exception: " + snapshot.getValue());
                updateErrorInterface();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {}
    };

}
