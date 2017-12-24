package com.forcetower.uefs.view.connected;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.view.settings.SettingsActivity;
import com.forcetower.uefs.adapters.ui.NavDrawerItem;
import com.forcetower.uefs.adapters.ui.NavigationDrawerAdapter;
import com.forcetower.uefs.helpers.PrefUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 29/11/2017.
 */

public class NavigationDrawerFragment extends Fragment implements NavigationDrawerAdapter.DrawerCallback{
    private static final String STATE_SELECTED_POSITION_KEY = "selected_position";
    private boolean userLearnedDrawer;

    private ViewGroup drawerContainer;
    private DrawerLayout rootView;
    private ActionBarDrawerToggle drawerToggle;

    private RecyclerView recyclerView;
    private NavigationDrawerAdapter drawerAdapter;

    private int selectedPosition;
    private boolean loadedSavedInstance = false;

    private Callbacks callbacks;

    private NavigationDrawerAdapter.OnNavDrawerClickListener navDrawerClickListener = new NavigationDrawerAdapter.OnNavDrawerClickListener() {
        @Override
        public void onDrawerItemClicked(View view, NavigationDrawerAdapter.ItemHolder vh, NavDrawerItem item, int position) {
            if (item.getOnClickListener() != null) {
                item.onClick();
                return;
            }
            selectItem(drawerAdapter.getCorrectPosition(position));
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (Callbacks)context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLearnedDrawer = PrefUtils.get(getContext(), "drawer_learned", false);
        if (savedInstanceState != null) {
            loadedSavedInstance = true;
            selectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        drawerAdapter = new NavigationDrawerAdapter(getActivity(), this, getDrawerItems());
        drawerAdapter.setClickListener(navDrawerClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(drawerAdapter);
        recyclerView.requestFocus();
    }

    private List<NavDrawerItem> getDrawerItems() {
        List<NavDrawerItem> items = new ArrayList<>();

        items.add(new NavDrawerItem(true));
        items.add(new NavDrawerItem(getString(R.string.title_schedule), R.drawable.ic_schedule_black_24dp, 1));
        items.add(new NavDrawerItem(getString(R.string.title_messages), R.drawable.ic_messages_black_24dp, 2));
        items.add(new NavDrawerItem(getString(R.string.title_grades), R.drawable.ic_grades_black_24dp, 3));
        items.add(new NavDrawerItem(getString(R.string.title_calendar), R.drawable.ic_calendar_black_24dp, 4));
        items.add(new NavDrawerItem(getString(R.string.title_disciplines), R.drawable.ic_book_open_black_24dp, 5));
        items.add(new NavDrawerItem(getString(R.string.settings), R.drawable.ic_settings_black_24dp, settingsListener));

        return items;
    }

    public void selectItem(int position) {
        selectedPosition = position;

        if (rootView != null) {
            rootView.closeDrawer(drawerContainer);
        }

        if (callbacks != null) {
            NavDrawerItem navItem = drawerAdapter.getItem(position + 1);
            callbacks.onNavigationDrawerItemSelected(navItem);
        }

        drawerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION_KEY, selectedPosition);
    }

    public boolean onBackPressed() {
        if (rootView.isDrawerOpen(drawerContainer)) {
            rootView.closeDrawer(drawerContainer, true);
            return true;
        }
        return false;
    }

    public void init(ViewGroup drawerContainer, DrawerLayout rootView) {
        this.drawerContainer = drawerContainer;
        this.rootView = rootView;

        drawerToggle = new ActionBarDrawerToggle(getActivity(), rootView, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;

                if (!userLearnedDrawer) {
                    userLearnedDrawer = true;
                    PrefUtils.save(getActivity(), "drawer_learned", true);
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0);
            }
        };

        if (!userLearnedDrawer && !loadedSavedInstance) {
            rootView.openDrawer(drawerContainer);
        }

        rootView.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });
        rootView.addDrawerListener(drawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getSelectedPosition() {
        return selectedPosition;
    }

    private NavigationDrawerAdapter.NavDrawerItemClickListener settingsListener = () -> SettingsActivity.startActivity(getContext());

    public interface Callbacks {
        void onNavigationDrawerItemSelected(NavDrawerItem item);
    }
}
