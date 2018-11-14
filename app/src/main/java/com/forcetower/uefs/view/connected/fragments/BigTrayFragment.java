package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentBigTrayBinding;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.ru.RUData;
import com.forcetower.uefs.ru.RUtils;
import com.forcetower.uefs.svc.BigTrayService;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.DateUtils;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.base.BigTrayViewModel;

import java.util.Calendar;

import javax.inject.Inject;

public class BigTrayFragment extends Fragment implements Injectable {
    @Inject
    UEFSViewModelFactory factory;

    private ActivityController controller;
    private FragmentBigTrayBinding binding;
    private boolean hasData = false;

    private BigTrayViewModel bigTrayViewModel;

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

        binding.btnOpenNotification.setOnClickListener(v -> startBigTrayService());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bigTrayViewModel = ViewModelProviders.of(this, factory).get(BigTrayViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        bigTrayViewModel.setRefreshing(true);
        bigTrayViewModel.getData().observe(this, this::bindData);
    }

    @Override
    public void onStop() {
        super.onStop();
        bigTrayViewModel.setRefreshing(false);
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
        Integer amount = -1;
        try { amount = Integer.parseInt(data.getCotas()); } catch (Exception ignored) {}
        Calendar calendar = data.getCalendar();
        int mealType = RUtils.getNextMealType(calendar);

        if (RUtils.isOpen(open, amount) && !data.isError()) {
            binding.tvRuState.setText(R.string.the_big_tray_is_open);
            binding.tvRuMeal.setText(data.getMealType());
            binding.tvRuAmount.setVisibility(View.VISIBLE);
            binding.tvRuAmount.setText(getString(R.string.ru_amount_format, amount));
            binding.tvRuMealTime.setText(RUtils.getNextMealTime(calendar));
            binding.tvRuMealPrice.setVisibility(View.VISIBLE);
            binding.tvRuApproxLabel.setVisibility(View.VISIBLE);
            binding.tvRuMealPrice.setText(RUtils.getPrice(mealType, amount));
            hasData = true;
        } else if (!data.isError()){
            binding.tvRuState.setText(R.string.the_big_tray_is_closed);
            binding.tvRuMeal.setText(RUtils.getNextMeal(requireContext(), mealType));
            binding.tvRuAmount.setVisibility(View.GONE);
            binding.tvRuMealTime.setText(RUtils.getNextMealTime(calendar));
            binding.tvRuMealPrice.setVisibility(View.GONE);
            binding.tvRuApproxLabel.setVisibility(View.GONE);
            hasData = true;
        } else if (!hasData){
            binding.tvRuState.setText(R.string.connection_error);
            binding.tvRuAmount.setVisibility(View.GONE);
            binding.tvRuMealTime.setText("");
            binding.tvRuMealPrice.setVisibility(View.GONE);
            binding.tvRuApproxLabel.setVisibility(View.GONE);
        }

        if (!data.isError())
            binding.tvRuLastUpdate.setText(getString(R.string.ru_last_update, DateUtils.formatDateTime(calendar.getTimeInMillis())));
    }

    private void startBigTrayService() {
        BigTrayService.startService(requireContext());
    }

}
