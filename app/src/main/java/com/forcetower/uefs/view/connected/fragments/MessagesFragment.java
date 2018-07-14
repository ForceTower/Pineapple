package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentMessagesBinding;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.ActivityController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class MessagesFragment extends Fragment implements Injectable {
    public static boolean DO_SELECT_AUTO = false;
    public static int SELECT_FRAGMENT_AUTO;
    private ActivityController controller;
    private FragmentMessagesBinding binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_messages, container, false);
        controller.getTabLayout().setVisibility(View.VISIBLE);
        controller.changeTitle(R.string.title_messages);
        configurePages();
        return binding.getRoot();
    }

    private void configurePages() {
        ViewPager viewPager = binding.viewPager;
        controller.getTabLayout().setupWithViewPager(viewPager);
        controller.getTabLayout().setTabGravity(TabLayout.GRAVITY_CENTER);
        controller.getTabLayout().setTabMode(TabLayout.MODE_SCROLLABLE);
        controller.getTabLayout().addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(controller.getTabLayout()));

        Fragment sagres = new MessagesLocalFragment();
        Fragment unes   = new MessagesServiceFragment();

        List<Pair<String, Fragment>> list = new ArrayList<>();
        list.add(new Pair<>(getString(R.string.sagres), sagres));
        list.add(new Pair<>(getString(R.string.unes),   unes));

        viewPager.setAdapter(new SectionFragmentAdapter(getChildFragmentManager(), list));
        if (DO_SELECT_AUTO) {
            DO_SELECT_AUTO = false;
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED))
                    viewPager.setCurrentItem(SELECT_FRAGMENT_AUTO, true);
            }, 500);
        }
    }

    private class SectionFragmentAdapter extends FragmentPagerAdapter {
        private List<Pair<String, Fragment>> fragments;

        SectionFragmentAdapter(FragmentManager fm, @NonNull List<Pair<String, Fragment>> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position).second;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).first;
        }
    }
}
