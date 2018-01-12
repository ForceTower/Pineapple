package com.forcetower.uefs.view.connected;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDetails;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassGroup;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.view.adapters.AllDisciplinesAdapter;
import com.forcetower.uefs.view.class_details.ClassDetailsActivity;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 17/12/2017.
 */

public class DisciplinesFragment extends Fragment {
    private RecyclerView rvAllDisciplines;

    public DisciplinesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disciplines, container, false);
        rvAllDisciplines = view.findViewById(R.id.rv_all_disciplines);

        setupAllDisciplines();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.disciplines_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem ref = menu.findItem(R.id.menu_refresh);
        if (ref != null) ref.setVisible(false);
        MenuItem down = menu.findItem(R.id.menu_download_all);
        if (down != null) down.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_download_all:
                new DownloadAllDisciplinesDialog().show(getFragmentManager(), "DownloadAllConfirmationFragment");
                break;
        }
        return false;
    }

    private void setupAllDisciplines() {
        SagresProfile profile = SagresProfile.getCurrentProfile();
        if (profile == null) {
            Log.e(APP_TAG, "Profile is null");
            return;
        }

        rvAllDisciplines.setNestedScrollingEnabled(false);
        AllDisciplinesAdapter adapter = new AllDisciplinesAdapter(getContext(), profile.getClassesDetails(), classClicked);
        rvAllDisciplines.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAllDisciplines.setAdapter(adapter);
        rvAllDisciplines.setNestedScrollingEnabled(false);
    }

    private OnDisciplineClickListener classClicked = (details, group) -> ClassDetailsActivity.startActivity(getContext(), details.getCode(), details.getSemester(), group.getType());

    public interface OnDisciplineClickListener {
        void onDisciplineClick(SagresClassDetails details, SagresClassGroup group);
    }
}
