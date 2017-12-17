package com.forcetower.uefs.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.domain.SagresSemester;
import com.forcetower.uefs.sagres_sdk.utility.SagresUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 02/12/2017.
 */
public class GradesFragment extends Fragment {
    private SwipeRefreshLayout refreshAllGrades;
    private boolean updating = false;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private RelativeLayout maskView;
    private Button downloadBtn;
    private ProgressBar progressBar;
    private TextView textCenter;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private HashMap<SagresSemester, List<SagresGrade>> grades;
    private List<String> gradesList;

    public GradesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades, container, false);

        viewPager = view.findViewById(R.id.container);
        tabLayout = view.findViewById(R.id.tabs);
        maskView = view.findViewById(R.id.mask_view);
        downloadBtn = view.findViewById(R.id.btn_download_grades);
        progressBar = view.findViewById(R.id.progress_bar);
        textCenter = view.findViewById(R.id.text_center_not_downloaded);
        refreshAllGrades = view.findViewById(R.id.refresh_all_grades);

        enableDisableSwipeRefresh(false);

        downloadBtn.setOnClickListener(downloadClick);
        if (Utils.isLollipop()) downloadBtn.setElevation(3);

        refreshAllGrades.setOnRefreshListener(refreshListener);

        grades = SagresProfile.getCurrentProfile().getAllSemestersGrades();
        if (grades != null && !grades.isEmpty()) {
            insertGradesOnInterface();
        }

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.grades_menu, menu);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_refresh);
        if (item != null) item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                downloadClick.onClick(null);
                break;
        }
        return false;
    }

    private void insertGradesOnInterface() {
        if (getActivity() == null) {
            Log.i(APP_TAG, "Null activity. It will not place the grades");
            return;
        }

        if (!isAdded() || isDetached()) {
            Log.i(APP_TAG, "Not added or is detached. It will not place the grades");
            return;
        }

        if (grades == null) {
            Log.i(APP_TAG, "Null grades. It will not place the grades");
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshAllGrades.setRefreshing(false);
                tabLayout.removeAllTabs();
                tabLayout.clearOnTabSelectedListeners();
                viewPager.clearOnPageChangeListeners();

                updating = false;
                tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
                if (Utils.isLollipop()) tabLayout.setElevation(10);

                Utils.fadeIn(tabLayout, getActivity());

                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

                Utils.fadeOut(maskView, getActivity());
                gradesList = asKeyList(grades);

                for (String semester : gradesList) {
                    TabLayout.Tab tab = tabLayout.newTab();
                    String start = semester.substring(0, 4);
                    tab.setText(start + "." + semester.substring(4));
                    tabLayout.addTab(tab);
                }

                sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
                viewPager.setAdapter(sectionsPagerAdapter);
            }
        });
    }

    public void enableDisableSwipeRefresh(boolean enable) {
        if (refreshAllGrades != null) {
            refreshAllGrades.setEnabled(enable);
        }
    }

    private List<String> asKeyList(HashMap<SagresSemester, List<SagresGrade>> grades) {
        List<String> list = new ArrayList<>();

        if (grades != null) {
            for (SagresSemester semester : grades.keySet()) {
                list.add(semester.getName());
            }
        }

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                try {
                    int i1 = Integer.parseInt(s.substring(0, 5));
                    int i2 = Integer.parseInt(t1.substring(0, 5));
                    if (i2 < i1)
                        return -1;
                    else
                        return 1;
                } catch (NumberFormatException e) {
                    return 1;
                }
            }
        });

        return list;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<GradeInnerFragment> fragments;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

            fragments = new ArrayList<>();
            for (String semester : gradesList) {
                GradeInnerFragment fragment = GradeInnerFragment.newInstance(semester);
                fragment.masterReference(GradesFragment.this);
                fragments.add(fragment);
            }

        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
            //String semester = gradesList.get(position);
            //return GradeInnerFragment.newInstance(semester);
        }

        @Override
        public int getCount() {
            return gradesList.size();
        }
    }

    private View.OnClickListener downloadClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (updating)
                return;

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    updating = true;
                    disableButtonAndShowProgress();
                    Pair<Integer, HashMap<SagresSemester, List<SagresGrade>>> pair = SagresUtility.connectAndGetGrades(null);
                    if (pair.first < 0) {
                        showOnToast(R.string.get_grades_failed);
                        enableButtonAndRemoveProgress();
                    } else {
                        if (SagresProfile.getCurrentProfile() != null) {
                            SagresProfile.getCurrentProfile().setAllSemestersGrades(pair.second);
                            SagresProfile.saveProfile();
                        }
                        gotGrades(pair.second);
                    }
                }
            };

            SagresPortalSDK.getExecutor().execute(runnable);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            downloadClick.onClick(null);
        }
    };

    private void disableButtonAndShowProgress() {
        if (getActivity() == null)
            return;

        if(!isAdded() || isRemoving())
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshAllGrades.setRefreshing(true);
                Utils.fadeOut(downloadBtn, getActivity());
                Utils.fadeIn(progressBar, getActivity());
                textCenter.setText(R.string.downloading_grades);
            }
        });
    }

    private void enableButtonAndRemoveProgress() {
        if (getActivity() == null)
            return;

        if(!isAdded() || isRemoving())
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updating = false;
                refreshAllGrades.setRefreshing(false);
                Utils.fadeIn(downloadBtn, getActivity());
                Utils.fadeOut(progressBar, getActivity());
                textCenter.setText(R.string.grades_not_downloaded_yet);
            }
        });
    }

    private void gotGrades(HashMap<SagresSemester, List<SagresGrade>> allSemestersGrades) {
        grades = allSemestersGrades;
        insertGradesOnInterface();
    }

    private void showOnToast(@StringRes final int id) {
        if (getActivity() == null)
            return;

        if(!isAdded() || isRemoving())
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
